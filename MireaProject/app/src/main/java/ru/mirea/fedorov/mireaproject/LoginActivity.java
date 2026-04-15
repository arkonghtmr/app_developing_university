package ru.mirea.fedorov.mireaproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import ru.mirea.fedorov.mireaproject.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private ActivityLoginBinding binding;
    @Nullable
    private FirebaseAuth firebaseAuth;
    private boolean firebaseReady;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseReady = initializeFirebaseAuth();
        binding.buttonCreateAccount.setOnClickListener(v -> createAccount(
                getEmail(),
                getPassword()
        ));
        binding.buttonSignIn.setOnClickListener(v -> signIn(getEmail(), getPassword()));
        binding.buttonSignOut.setOnClickListener(v -> signOut());
        binding.buttonVerifyEmail.setOnClickListener(v -> sendEmailVerification());
        binding.buttonContinue.setOnClickListener(v -> openMainScreen());

        if (firebaseReady && firebaseAuth != null) {
            updateUI(firebaseAuth.getCurrentUser());
        } else {
            showMissingFirebaseConfig();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseReady && firebaseAuth != null) {
            updateUI(firebaseAuth.getCurrentUser());
        }
    }

    private boolean initializeFirebaseAuth() {
        try {
            List<FirebaseApp> apps = FirebaseApp.getApps(this);
            FirebaseApp firebaseApp = apps.isEmpty()
                    ? FirebaseApp.initializeApp(this)
                    : FirebaseApp.getInstance();

            if (firebaseApp == null) {
                return false;
            }
            firebaseAuth = FirebaseAuth.getInstance(firebaseApp);
            return true;
        } catch (IllegalStateException exception) {
            Log.w(TAG, "Firebase is not configured", exception);
            return false;
        }
    }

    private String getEmail() {
        return binding.editEmail.getText() == null
                ? ""
                : binding.editEmail.getText().toString().trim();
    }

    private String getPassword() {
        return binding.editPassword.getText() == null
                ? ""
                : binding.editPassword.getText().toString();
    }

    private boolean validateForm() {
        binding.layoutEmail.setError(null);
        binding.layoutPassword.setError(null);

        boolean valid = true;
        if (TextUtils.isEmpty(getEmail())) {
            binding.layoutEmail.setError(getString(R.string.login_email_required));
            valid = false;
        }

        String password = getPassword();
        if (TextUtils.isEmpty(password)) {
            binding.layoutPassword.setError(getString(R.string.login_password_required));
            valid = false;
        } else if (password.length() < 6) {
            binding.layoutPassword.setError(getString(R.string.login_password_too_short));
            valid = false;
        }

        return valid;
    }

    private void createAccount(String email, String password) {
        if (!firebaseReady || firebaseAuth == null) {
            showMissingFirebaseConfig();
            return;
        }
        if (!validateForm()) {
            return;
        }

        Log.d(TAG, "createAccount:" + email);
        binding.statusTextView.setText(R.string.login_progress);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        updateUI(firebaseAuth.getCurrentUser());
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        updateUI(null);
                        showAuthError(task.getException());
                    }
                });
    }

    private void signIn(String email, String password) {
        if (!firebaseReady || firebaseAuth == null) {
            showMissingFirebaseConfig();
            return;
        }
        if (!validateForm()) {
            return;
        }

        Log.d(TAG, "signIn:" + email);
        binding.statusTextView.setText(R.string.login_progress);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        updateUI(firebaseAuth.getCurrentUser());
                        openMainScreen();
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        updateUI(null);
                        showAuthError(task.getException());
                    }
                });
    }

    private void signOut() {
        if (firebaseAuth != null) {
            firebaseAuth.signOut();
        }
        updateUI(null);
    }

    private void sendEmailVerification() {
        if (!firebaseReady || firebaseAuth == null) {
            showMissingFirebaseConfig();
            return;
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            updateUI(null);
            return;
        }

        binding.buttonVerifyEmail.setEnabled(false);
        user.sendEmailVerification()
                .addOnCompleteListener(this, task -> {
                    binding.buttonVerifyEmail.setEnabled(!user.isEmailVerified());
                    if (task.isSuccessful()) {
                        Toast.makeText(this, R.string.login_verification_sent, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "sendEmailVerification", task.getException());
                        Toast.makeText(this, R.string.login_verification_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(@Nullable FirebaseUser user) {
        if (user != null) {
            binding.statusTextView.setText(R.string.login_signed_in);
            binding.detailTextView.setText(getString(
                    R.string.login_email_status_fmt,
                    user.getEmail(),
                    user.isEmailVerified()
            ) + "\n" + getString(R.string.login_firebase_status_fmt, user.getUid()));
            binding.emailPasswordButtons.setVisibility(View.GONE);
            binding.emailPasswordFields.setVisibility(View.GONE);
            binding.signedInButtons.setVisibility(View.VISIBLE);
            binding.buttonVerifyEmail.setEnabled(!user.isEmailVerified());
        } else {
            binding.statusTextView.setText(R.string.login_signed_out);
            binding.detailTextView.setText(null);
            binding.emailPasswordButtons.setVisibility(View.VISIBLE);
            binding.emailPasswordFields.setVisibility(View.VISIBLE);
            binding.signedInButtons.setVisibility(View.GONE);
            binding.buttonCreateAccount.setEnabled(firebaseReady);
            binding.buttonSignIn.setEnabled(firebaseReady);
        }
    }

    private void showMissingFirebaseConfig() {
        binding.statusTextView.setText(R.string.login_config_missing_status);
        binding.detailTextView.setText(R.string.login_config_missing_detail);
        binding.emailPasswordButtons.setVisibility(View.VISIBLE);
        binding.emailPasswordFields.setVisibility(View.VISIBLE);
        binding.signedInButtons.setVisibility(View.GONE);
        binding.buttonCreateAccount.setEnabled(false);
        binding.buttonSignIn.setEnabled(false);
    }

    private void showAuthError(@Nullable Exception exception) {
        String message = exception == null || exception.getLocalizedMessage() == null
                ? getString(R.string.login_auth_failed)
                : exception.getLocalizedMessage();
        binding.statusTextView.setText(getString(R.string.login_error_template, message));
        Toast.makeText(this, R.string.login_auth_failed, Toast.LENGTH_SHORT).show();
    }

    private void openMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
