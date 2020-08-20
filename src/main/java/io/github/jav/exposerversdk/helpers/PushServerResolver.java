package io.github.jav.exposerversdk.helpers;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

public interface PushServerResolver {
    public CompletableFuture<String> postAsync(URL url, String json);
}
