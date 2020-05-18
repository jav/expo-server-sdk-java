package io.github.jav.exposerversdk;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

public interface PushServerResolver {
    public CompletableFuture<String> postAsync(URL url, String json);
}
