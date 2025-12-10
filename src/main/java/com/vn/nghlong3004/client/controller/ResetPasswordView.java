package com.vn.nghlong3004.client.controller;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/10/2025
 */
public interface ResetPasswordView {
  void showLoading(boolean isLoading);

  void showSuccess(String messageKey);

  void showWarning(String messageKey);

  void showError(String messageKey);

  void showInfo(String messageKey);

  void closeModal();

  String getPassword();

  String getRePassword();
}
