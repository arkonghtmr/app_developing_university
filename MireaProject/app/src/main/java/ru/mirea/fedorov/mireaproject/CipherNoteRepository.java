package ru.mirea.fedorov.mireaproject;

import android.content.Context;
import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherNoteRepository {

    private static final String DIRECTORY_NAME = "mirea_cipher_notes";
    private static final String FILE_EXTENSION = ".mnote";
    private static final int IV_SIZE = 12;
    private static final int TAG_SIZE = 128;

    private final Context appContext;
    private final SecretKeySpec secretKeySpec;
    private final SecureRandom secureRandom = new SecureRandom();

    public CipherNoteRepository(Context context) {
        appContext = context.getApplicationContext();
        secretKeySpec = new SecretKeySpec(buildKeyBytes(), "AES");
    }

    public String getDirectoryPath() throws IOException {
        return getNotesDirectory().getAbsolutePath();
    }

    public void saveNote(String title, String text) throws IOException, GeneralSecurityException {
        JSONObject payload = new JSONObject();
        try {
            payload.put("title", title.trim());
            payload.put("text", text.trim());
            payload.put("updatedAt", System.currentTimeMillis());
        } catch (JSONException e) {
            throw new IOException("Не удалось подготовить заметку", e);
        }

        byte[] encrypted = encrypt(payload.toString());
        File noteFile = buildNoteFile(title);
        try (FileOutputStream outputStream = new FileOutputStream(noteFile, false)) {
            outputStream.write(encrypted);
        }
    }

    public List<CipherNote> getNotes() throws IOException {
        File directory = getNotesDirectory();
        File[] files = directory.listFiles((dir, name) -> name.endsWith(FILE_EXTENSION));
        List<CipherNote> notes = new ArrayList<>();
        if (files == null) {
            return notes;
        }

        for (File file : files) {
            try {
                notes.add(readNote(file));
            } catch (GeneralSecurityException | JSONException e) {
                // One broken file should not stop the rest of the list from loading.
            }
        }
        notes.sort(Comparator.comparingLong(CipherNote::getUpdatedAt).reversed());
        return notes;
    }

    private CipherNote readNote(File file) throws IOException, GeneralSecurityException, JSONException {
        byte[] bytes = readFileBytes(file);
        String payload = decrypt(bytes);
        JSONObject jsonObject = new JSONObject(payload);
        return new CipherNote(
                jsonObject.optString("title", file.getName()),
                jsonObject.optString("text", ""),
                jsonObject.optLong("updatedAt", file.lastModified()),
                file
        );
    }

    private File getNotesDirectory() throws IOException {
        File documentsDirectory = appContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (documentsDirectory == null) {
            throw new IOException(appContext.getString(R.string.files_storage_unavailable));
        }
        File notesDirectory = new File(documentsDirectory, DIRECTORY_NAME);
        if (!notesDirectory.exists() && !notesDirectory.mkdirs()) {
            throw new IOException(appContext.getString(R.string.files_storage_unavailable));
        }
        return notesDirectory;
    }

    private File buildNoteFile(String title) throws IOException {
        String sanitizedTitle = title.trim()
                .replaceAll("[\\\\/:*?\"<>|]", "_")
                .replaceAll("\\s+", "_");
        if (sanitizedTitle.isEmpty()) {
            sanitizedTitle = "note";
        }
        return new File(getNotesDirectory(), sanitizedTitle + FILE_EXTENSION);
    }

    private byte[] buildKeyBytes() {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] digest = messageDigest.digest(
                    "Fedorov_Anton_BSBO-09-23_MireaProject".getBytes(StandardCharsets.UTF_8)
            );
            return Arrays.copyOf(digest, 32);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }

    private byte[] encrypt(String plainText) throws GeneralSecurityException {
        byte[] iv = new byte[IV_SIZE];
        secureRandom.nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new GCMParameterSpec(TAG_SIZE, iv));
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        byte[] result = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
        return result;
    }

    private String decrypt(byte[] bytes) throws GeneralSecurityException {
        if (bytes.length <= IV_SIZE) {
            throw new GeneralSecurityException("Payload is too short");
        }
        byte[] iv = Arrays.copyOfRange(bytes, 0, IV_SIZE);
        byte[] encrypted = Arrays.copyOfRange(bytes, IV_SIZE, bytes.length);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new GCMParameterSpec(TAG_SIZE, iv));
        byte[] plainBytes = cipher.doFinal(encrypted);
        return new String(plainBytes, StandardCharsets.UTF_8);
    }

    private byte[] readFileBytes(File file) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            byte[] bytes = outputStream.toByteArray();
            if (bytes.length == 0) {
                throw new IOException("Файл пустой");
            }
            return bytes;
        }
    }
}
