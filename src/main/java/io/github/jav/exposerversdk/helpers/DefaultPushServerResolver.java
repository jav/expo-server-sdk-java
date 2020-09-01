package io.github.jav.exposerversdk.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;

public class DefaultPushServerResolver implements PushServerResolver {
    public CompletableFuture<String> postAsync(URL url, String json) throws CompletionException {
        String finalJson = json;

        CompletableFuture<String> retCompletableFuture
                = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);

            try (OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = finalJson.getBytes("utf-8");
                os.write(input, 0, input.length);
            } catch (IOException e) {
                throw new CompletionException(e);
            }

            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    sb.append(inputLine);
                in.close();
            } catch (IOException e) {
                throw new CompletionException(e);

            } finally {
                urlConnection.disconnect();
                retCompletableFuture.complete(sb.toString());
            }
            return null;
        });
        return retCompletableFuture;
    }

}
