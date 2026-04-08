package ru.mirea.fedorov.mireaproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mirea.fedorov.mireaproject.databinding.DialogCipherNoteBinding;
import ru.mirea.fedorov.mireaproject.databinding.FragmentFilesBinding;
import ru.mirea.fedorov.mireaproject.databinding.ItemCipherNoteBinding;

public class FilesFragment extends Fragment {

    private FragmentFilesBinding binding;
    private CipherNoteRepository repository;
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFilesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new CipherNoteRepository(requireContext());
        binding.fabAddNote.setOnClickListener(v -> showCreateDialog());
        try {
            binding.textDirectoryInfo.setText(getString(
                    R.string.files_directory_template,
                    repository.getDirectoryPath()
            ));
        } catch (IOException e) {
            binding.textDirectoryInfo.setText(getString(
                    R.string.files_status_error_template,
                    e.getMessage()
            ));
        }
        loadNotes();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null) {
            loadNotes();
        }
    }

    private void showCreateDialog() {
        if (binding == null) {
            return;
        }

        DialogCipherNoteBinding dialogBinding = DialogCipherNoteBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.files_dialog_title)
                .setView(dialogBinding.getRoot())
                .setNegativeButton(R.string.files_dialog_cancel, null)
                .setPositiveButton(R.string.files_dialog_save, null)
                .create();

        dialog.setOnShowListener(ignored -> dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    String fileName = dialogBinding.editFileName.getText().toString().trim();
                    String text = dialogBinding.editFileText.getText().toString().trim();

                    dialogBinding.layoutFileName.setError(null);
                    dialogBinding.layoutFileText.setError(null);

                    boolean hasError = false;
                    if (TextUtils.isEmpty(fileName)) {
                        dialogBinding.layoutFileName.setError(getString(R.string.files_name_required));
                        hasError = true;
                    }
                    if (TextUtils.isEmpty(text)) {
                        dialogBinding.layoutFileText.setError(getString(R.string.files_text_required));
                        hasError = true;
                    }
                    if (hasError) {
                        return;
                    }
                    saveNote(fileName, text, dialog);
                }));

        dialog.show();
    }

    private void saveNote(String title, String text, AlertDialog dialog) {
        if (binding == null) {
            return;
        }
        binding.textFilesStatus.setText(R.string.files_status_loading);
        ioExecutor.execute(() -> {
            try {
                repository.saveNote(title, text);
                if (binding == null) {
                    return;
                }
                binding.getRoot().post(() -> {
                    if (binding == null) {
                        return;
                    }
                    dialog.dismiss();
                    binding.textFilesStatus.setText(R.string.files_status_saved);
                    loadNotes();
                });
            } catch (IOException | GeneralSecurityException e) {
                if (binding == null) {
                    return;
                }
                binding.getRoot().post(() -> {
                    if (binding != null) {
                        binding.textFilesStatus.setText(getString(
                                R.string.files_status_error_template,
                                e.getMessage()
                        ));
                    }
                });
            }
        });
    }

    private void loadNotes() {
        if (binding == null) {
            return;
        }
        binding.textFilesStatus.setText(R.string.files_status_loading);
        ioExecutor.execute(() -> {
            try {
                List<CipherNote> notes = repository.getNotes();
                if (binding == null) {
                    return;
                }
                binding.getRoot().post(() -> renderNotes(notes));
            } catch (IOException e) {
                if (binding == null) {
                    return;
                }
                binding.getRoot().post(() -> {
                    if (binding != null) {
                        binding.textFilesStatus.setText(getString(
                                R.string.files_status_error_template,
                                e.getMessage()
                        ));
                    }
                });
            }
        });
    }

    private void renderNotes(List<CipherNote> notes) {
        if (binding == null) {
            return;
        }

        binding.notesContainer.removeAllViews();
        if (notes.isEmpty()) {
            binding.textEmptyState.setVisibility(View.VISIBLE);
            binding.textFilesStatus.setText(R.string.files_status_empty);
            return;
        }

        binding.textEmptyState.setVisibility(View.GONE);
        binding.textFilesStatus.setText(getString(R.string.files_status_count_template, notes.size()));
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (CipherNote note : notes) {
            ItemCipherNoteBinding itemBinding = ItemCipherNoteBinding.inflate(
                    inflater,
                    binding.notesContainer,
                    false
            );
            itemBinding.textNoteTitle.setText(note.getTitle());
            itemBinding.textNoteMeta.setText(getString(
                    R.string.files_note_meta_template,
                    formatDate(note.getUpdatedAt()),
                    buildPreview(note.getText())
            ));
            itemBinding.cardNote.setOnClickListener(v -> showNoteDialog(note));
            binding.notesContainer.addView(itemBinding.getRoot());
        }
    }

    private void showNoteDialog(CipherNote note) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(note.getTitle())
                .setMessage(note.getText())
                .setPositiveButton(R.string.files_open_close, null)
                .show();
    }

    private String formatDate(long updatedAt) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance(
                DateFormat.SHORT,
                DateFormat.SHORT,
                new Locale("ru", "RU")
        );
        return dateFormat.format(updatedAt);
    }

    private String buildPreview(String text) {
        String oneLineText = text.replace('\n', ' ').trim();
        if (oneLineText.length() <= 90) {
            return oneLineText;
        }
        return oneLineText.substring(0, 87) + "...";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ioExecutor.shutdownNow();
    }
}
