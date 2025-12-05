package com.vn.nghlong3004.client.game;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.*;
import javax.swing.*;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
public class GameLaunch {
  public static void run() {
    FlatRobotoFont.install();
    FlatLaf.registerCustomDefaultsSource("themes");
    FlatMacLightLaf.setup();
    UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
    GameWindow gameWindow = GameFactory.createGameWindow();
    EventQueue.invokeLater(gameWindow::open);
    gameWindow.registerCloseThread();
  }
}
