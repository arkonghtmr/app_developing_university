package ru.mirea.fedorov.mireaproject;

import java.io.File;

public class CipherNote {

    private final String title;
    private final String text;
    private final long updatedAt;
    private final File file;

    public CipherNote(String title, String text, long updatedAt, File file) {
        this.title = title;
        this.text = text;
        this.updatedAt = updatedAt;
        this.file = file;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public File getFile() {
        return file;
    }
}
