package com.vn.nghlong3004.client.configuration;

import com.vn.nghlong3004.client.service.HttpService;
import com.vn.nghlong3004.client.service.impl.HttpServiceImpl;
import lombok.Getter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/8/2025
 */
public class WebConfiguration {
  @Getter private final HttpService httpService;

  public static WebConfiguration getInstance() {
    return Holder.INSTANCE;
  }

  private WebConfiguration() {
    httpService = new HttpServiceImpl();
  }

  private static class Holder {
    private static final WebConfiguration INSTANCE = new WebConfiguration();
  }
}
