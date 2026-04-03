package ru.mirea.fedorov.lesson4.cryptoloader;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MyLoader extends AsyncTaskLoader<String> {

    public static final String ARG_CIPHER = "cipher_text";
    public static final String ARG_KEY = "key_bytes";

    private final byte[] cipherText;
    private final byte[] keyBytes;

    public MyLoader(@NonNull Context context, Bundle args) {
        super(context);
        cipherText = args != null ? args.getByteArray(ARG_CIPHER) : null;
        keyBytes = args != null ? args.getByteArray(ARG_KEY) : null;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        if (cipherText == null || keyBytes == null) {
            return "Отсутствуют данные для дешифровки";
        }

        SystemClock.sleep(2000);
        SecretKey originalKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
        return MainActivity.decryptMsg(cipherText, originalKey);
    }
}
