package com.vn.nghlong3004.client.model.request;

import lombok.Builder;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
@Builder
public record RegisterRequest(
    String email, String password, String birthday, String fullName, Integer gender) {}
