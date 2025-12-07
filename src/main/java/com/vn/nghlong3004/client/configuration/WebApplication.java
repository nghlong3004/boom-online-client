package com.vn.nghlong3004.client.configuration;

import com.vn.nghlong3004.client.service.HttpService;
import lombok.Getter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
public class WebApplication {
  @Getter private final HttpService httpService;

  public static WebApplication getInstance() {
    return Holder.INSTANCE;
  }

  private WebApplication() {
    httpService = new HttpService();
  }

  private static class Holder {
    private static final WebApplication INSTANCE = new WebApplication();
  }
}
