package com.vn.nghlong3004.client.game;

import com.vn.nghlong3004.client.configuration.ApplicationConfiguration;
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
public class GameLoop implements Runnable {

  private final double SECOND = 1E9;
  private final double FRAMES_PER_NANO_TIME =
      SECOND / ApplicationConfiguration.getInstance().getFps();
  private final double UPDATE_PER_NANO_TIME =
      SECOND / ApplicationConfiguration.getInstance().getUps();

  private final GamePanel gamePanel;

  private long lastTime;
  private long currentTime;
  // This is the difference between currentTime and lastTime
  private double secondDelta = 0;
  private double frameDelta = 0;
  private double updateDelta = 0;
  // This is the per second
  private int frameCount = 0;
  private int updateCount = 0;

  @Override
  public void run() {
    lastTime = System.nanoTime();
    currentTime = lastTime;

    while (!Thread.currentThread().isInterrupted()) {

      // 1. Calculate time differences
      updateTimer();
      // 2. Process game logic (can run multiple times to catch up)
      processUpdate();
      // 3. Process rendering (only when it is time)
      processRender();
      // 4. display fps and ups (once per second)
      displayFPSandUPS();
    }
  }

  private void updateTimer() {
    currentTime = System.nanoTime();
    long elapsedNanoTime = currentTime - lastTime;
    lastTime = currentTime;

    frameDelta += elapsedNanoTime / FRAMES_PER_NANO_TIME;
    updateDelta += elapsedNanoTime / UPDATE_PER_NANO_TIME;
    secondDelta += elapsedNanoTime / SECOND;
  }

  private void processUpdate() {
    while (updateDelta >= 1.0) {
      --updateDelta;
      ++updateCount;
      gamePanel.update();
    }
  }

  private void processRender() {
    if (frameDelta >= 1.0) {
      --frameDelta;
      ++frameCount;
      gamePanel.repaint();
    }
  }

  private void displayFPSandUPS() {
    if (secondDelta >= 1.0) {
      --secondDelta;
      log.info("FPS: {} | UPS: {}", frameCount, updateCount);
      frameCount = 0;
      updateCount = 0;
    }
  }
}
