package io.github.jav.exposerversdk.helpers;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DefaultPushServerResolver implements PushServerResolver {
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    public CompletableFuture<String> postAsync(URL url, String json) throws CompletionException {
        String finalJson = json;

        CompletableFuture<String> retCompletableFuture
                = new CompletableFuture<>();

        threadPool.submit(() -> {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);

                try (OutputStream os = urlConnection.getOutputStream()) {
                    byte[] input = finalJson.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                StringBuilder sb = new StringBuilder();
                try (InputStream is = urlConnection.getInputStream()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(is));

                    String inputLine;
                    while ((inputLine = in.readLine()) != null)
                        sb.append(inputLine);
                }

                retCompletableFuture.complete(sb.toString());
            } catch (IOException e) {
                StringBuilder sb = new StringBuilder();
                try (InputStream is = urlConnection.getErrorStream()) {
                    if (is != null) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(is));

                        String inputLine;
                        while ((inputLine = in.readLine()) != null)
                            sb.append(inputLine);

                        retCompletableFuture.complete(sb.toString());
                    } else {
                        retCompletableFuture.completeExceptionally(e);
                    }
                } catch (Throwable t) {
                    retCompletableFuture.completeExceptionally(t);
                }
            } catch (Throwable e) {
                retCompletableFuture.completeExceptionally(e);
            } finally {
                urlConnection.disconnect();
            }
            return null;
        });
        return retCompletableFuture;
    }

}
