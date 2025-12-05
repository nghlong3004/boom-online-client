package com.vn.nghlong3004.client.game;

import com.vn.nghlong3004.client.constant.GameConstant;
import com.vn.nghlong3004.client.constant.ImageConstant;
import com.vn.nghlong3004.client.util.ImageUtil;
import javax.swing.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
@Slf4j
@RequiredArgsConstructor
public class GameWindow extends JFrame {
  private final Thread thread;

  protected void open() {
    thread.start();
    setVisible(true);
  }

  protected void setting(JPanel panel) {
    setTitle(GameConstant.TITLE);
    add(panel);
    setResizable(false);
    pack();
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setIconImage(ImageUtil.loadImage(ImageConstant.IMAGE_TITLE));
  }

  protected void registerCloseThread() {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  try {
                    log.info("Closing game...");
                    thread.interrupt();
                    thread.join();
                  } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                  }
                }));
  }
}
