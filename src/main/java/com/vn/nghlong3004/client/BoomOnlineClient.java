package com.vn.nghlong3004.client;

import com.vn.nghlong3004.client.game.GameLaunch;
import javax.swing.*;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/3/2025
 */
public class BoomOnlineClient {
  public static void main(String[] args) {
    SwingUtilities.invokeLater(GameLaunch::run);
  }
}
