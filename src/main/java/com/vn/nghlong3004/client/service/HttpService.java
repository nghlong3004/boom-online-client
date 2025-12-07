package com.vn.nghlong3004.client.service;

import com.google.gson.Gson;
import com.vn.nghlong3004.client.configuration.ApplicationConfiguration;
import com.vn.nghlong3004.client.model.request.RegisterRequest;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpService {

  private final HttpClient client;
  private final Gson gson;
  private final String BASE_URL;

  public HttpService() {
    this.BASE_URL = ApplicationConfiguration.getInstance().getServerUrl();
    this.client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    this.gson = ApplicationConfiguration.getInstance().getGson();
    log.info("HttpService initialized with Base URL: {}", BASE_URL);
  }

  public CompletableFuture<String> sendRegisterRequest(RegisterRequest registerRequest) {
    log.info("Initiating registration request for email: {}", registerRequest.email());

    String jsonBody = gson.toJson(registerRequest);

    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/auth/register"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

    return client
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(
            response -> {
              int statusCode = response.statusCode();
              log.info("Received response for registration. Status Code: {}", statusCode);

              if (statusCode >= 400) {
                log.error("Registration failed. Status: {}, Body: {}", statusCode, response.body());
                throw new RuntimeException(response.body());
              }

              log.debug("Registration successful. Response body: {}", response.body());
              return response.body();
            });
  }
}
