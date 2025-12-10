package com.vn.nghlong3004.client.controller;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/10/2025
 */
public interface RegisterView {
  void showLoading(boolean isLoading);

  void showSuccessMessage();

  void showWarning(String messageKey);

  void navigateToLogin();

  void clearForm();

  String getEmail();

  String getFullName();

  String getPassword();

  String getRePassword();

  String getBirthday();

  int getGender();
}
