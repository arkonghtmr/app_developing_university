package ru.mirea.fedorov.mireaproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.mirea.fedorov.mireaproject.databinding.FragmentNetworkBinding;

public class NetworkFragment extends Fragment {

    private static final String API_URL = "https://api.github.com/repos/android/nowinandroid";

    private FragmentNetworkBinding binding;
    private final ExecutorService networkExecutor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentNetworkBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonRefreshNetwork.setOnClickListener(v -> loadRepositoryInfo());
        loadRepositoryInfo();
    }

    private void loadRepositoryInfo() {
        if (binding == null) {
            return;
        }

        binding.buttonRefreshNetwork.setEnabled(false);
        binding.textNetworkStatus.setText(R.string.network_status_loading);
        String emptyValue = getString(R.string.network_empty_value);
        networkExecutor.execute(() -> {
            try {
                RepositoryInfo repositoryInfo = parseRepositoryInfo(downloadJson(), emptyValue);
                if (binding == null) {
                    return;
                }
                binding.getRoot().post(() -> renderRepositoryInfo(repositoryInfo));
            } catch (IOException | JSONException exception) {
                if (binding == null) {
                    return;
                }
                binding.getRoot().post(() -> renderError(exception));
            }
        });
    }

    private String downloadJson() throws IOException {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(10_000);
            connection.setRequestProperty("Accept", "application/vnd.github+json");
            connection.setRequestProperty("User-Agent", "MireaProject-Practice7");

            int responseCode = connection.getResponseCode();
            InputStream stream = responseCode >= 200 && responseCode < 300
                    ? connection.getInputStream()
                    : connection.getErrorStream();
            String responseBody = readStream(stream);
            if (responseCode < 200 || responseCode >= 300) {
                throw new IOException("HTTP " + responseCode + ": " + buildPreview(responseBody));
            }
            return responseBody;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String readStream(@Nullable InputStream stream) throws IOException {
        if (stream == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
        )) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        }
        return builder.toString();
    }

    private RepositoryInfo parseRepositoryInfo(String json, String emptyValue) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        return new RepositoryInfo(
                jsonObject.optString("full_name", emptyValue),
                jsonObject.optString("description", emptyValue),
                jsonObject.optInt("stargazers_count"),
                jsonObject.optString("language", emptyValue),
                jsonObject.optString("updated_at", emptyValue),
                jsonObject.optString("html_url", API_URL)
        );
    }

    private void renderRepositoryInfo(RepositoryInfo repositoryInfo) {
        if (binding == null) {
            return;
        }

        binding.buttonRefreshNetwork.setEnabled(true);
        binding.textNetworkStatus.setText(R.string.network_status_loaded);
        binding.textRepoName.setText(repositoryInfo.name);
        binding.textRepoDescription.setText(repositoryInfo.description);
        binding.textRepoStats.setText(getString(
                R.string.network_stats_template,
                repositoryInfo.stars,
                repositoryInfo.language,
                repositoryInfo.updatedAt
        ));
        binding.textRepoUrl.setText(getString(R.string.network_url_template, repositoryInfo.url));
    }

    private void renderError(Exception exception) {
        if (binding == null) {
            return;
        }

        binding.buttonRefreshNetwork.setEnabled(true);
        binding.textNetworkStatus.setText(getString(
                R.string.network_status_error_template,
                exception.getMessage()
        ));
    }

    private String buildPreview(String text) {
        String oneLineText = text.replace('\n', ' ').trim();
        if (oneLineText.length() <= 120) {
            return oneLineText;
        }
        return oneLineText.substring(0, 117) + "...";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        networkExecutor.shutdownNow();
    }

    private static class RepositoryInfo {
        private final String name;
        private final String description;
        private final int stars;
        private final String language;
        private final String updatedAt;
        private final String url;

        private RepositoryInfo(String name, String description, int stars,
                               String language, String updatedAt, String url) {
            this.name = name;
            this.description = description;
            this.stars = stars;
            this.language = language;
            this.updatedAt = updatedAt;
            this.url = url;
        }
    }
}
