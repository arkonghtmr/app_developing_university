package ru.mirea.fedorov.mireaproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.mirea.fedorov.mireaproject.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private static final String PREFS_NAME = "profile_preferences";
    private static final String KEY_NAME = "profile_name";
    private static final String KEY_GROUP = "profile_group";
    private static final String KEY_TRACK = "profile_track";
    private static final String KEY_GOAL = "profile_goal";

    private FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadProfile();
        binding.buttonSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void loadProfile() {
        SharedPreferences preferences = getPreferences();
        binding.editProfileName.setText(preferences.getString(
                KEY_NAME,
                getString(R.string.profile_default_name)
        ));
        binding.editProfileGroup.setText(preferences.getString(
                KEY_GROUP,
                getString(R.string.profile_default_group)
        ));
        binding.editProfileTrack.setText(preferences.getString(
                KEY_TRACK,
                getString(R.string.profile_default_track)
        ));
        binding.editProfileGoal.setText(preferences.getString(
                KEY_GOAL,
                getString(R.string.profile_default_goal)
        ));
        binding.textProfileStatus.setText(R.string.profile_status_default);
    }

    private void saveProfile() {
        getPreferences().edit()
                .putString(KEY_NAME, binding.editProfileName.getText().toString().trim())
                .putString(KEY_GROUP, binding.editProfileGroup.getText().toString().trim())
                .putString(KEY_TRACK, binding.editProfileTrack.getText().toString().trim())
                .putString(KEY_GOAL, binding.editProfileGoal.getText().toString().trim())
                .apply();
        binding.textProfileStatus.setText(R.string.profile_status_saved);
    }

    private SharedPreferences getPreferences() {
        return requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
