package com.vn.nghlong3004.client.service;

import com.vn.nghlong3004.client.model.request.ForgotPasswordRequest;
import com.vn.nghlong3004.client.model.request.LoginRequest;
import com.vn.nghlong3004.client.model.request.RegisterRequest;
import java.util.concurrent.CompletableFuture;

public interface HttpService {

  CompletableFuture<String> sendRegisterRequest(RegisterRequest registerRequest);

  CompletableFuture<String> sendLoginRequest(LoginRequest loginRequest);

  CompletableFuture<String> sendForgotPassword(ForgotPasswordRequest forgotPasswordRequest);
}
