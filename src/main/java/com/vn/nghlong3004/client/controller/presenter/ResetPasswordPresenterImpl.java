package com.vn.nghlong3004.client.controller.presenter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.vn.nghlong3004.client.context.ApplicationContext;
import com.vn.nghlong3004.client.controller.ResetPasswordPresenter;
import com.vn.nghlong3004.client.controller.ResetPasswordView;
import com.vn.nghlong3004.client.model.request.ResetPasswordRequest;
import com.vn.nghlong3004.client.model.response.ErrorResponse;
import com.vn.nghlong3004.client.service.HttpService;
import com.vn.nghlong3004.client.util.LanguageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/10/2025
 */
@Slf4j
@RequiredArgsConstructor
public class ResetPasswordPresenterImpl implements ResetPasswordPresenter {

  private final ResetPasswordView view;
  private final HttpService httpService;
  private final Gson gson;

  @Override
  public void handleSubmit() {
    String password = view.getPassword();
    String rePassword = view.getRePassword();

    if (password.length() <= 6) {
      view.showWarning("validation_password_length_error");
      return;
    }

    if (!password.equals(rePassword)) {
      view.showWarning("validation_password_match_error");
      return;
    }

    view.showInfo("handler");
    view.showLoading(true);

    String email = ApplicationContext.getInstance().getEmail();
    String token = ApplicationContext.getInstance().getVerificationToken();
    String lang = LanguageUtil.getInstance().getCurrentLocale().getLanguage();

    ResetPasswordRequest request = new ResetPasswordRequest(token, email, password, lang);

    httpService
        .sendResetPassword(request)
        .thenAccept(
            responseBody -> {
              view.showLoading(false);
              view.showSuccess("register_successfully");
              view.closeModal();
            })
        .exceptionally(
            e -> {
              view.showLoading(false);
              Throwable throwable = e.getCause();
              String rawMessage = (throwable != null) ? throwable.getMessage() : e.getMessage();
              String messageKey = mapErrorToMessageKey(rawMessage);
              view.showError(messageKey);
              return null;
            });
  }

  private String mapErrorToMessageKey(String errorBody) {
    if (errorBody == null) return "server_error";

    if (errorBody.contains("ConnectException") || errorBody.contains("Network")) {
      return "server_error";
    }

    try {
      ErrorResponse errorResponse = gson.fromJson(errorBody, ErrorResponse.class);

      if (errorResponse != null && errorResponse.code() != null) {
        return switch (errorResponse.code()) {
          case "TokenIncorrect" -> "reset_password_bad_credentials";
          default -> "unknown_error";
        };
      }
    } catch (JsonSyntaxException ignored) {
      log.error("Cannot parse error body: {}", errorBody);
      return "server_error";
    }

    return "login_failed";
  }
}
