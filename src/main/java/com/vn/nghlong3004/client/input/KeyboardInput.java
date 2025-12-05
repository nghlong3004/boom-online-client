package com.vn.nghlong3004.client.input;

import com.vn.nghlong3004.client.game.GamePanel;
import java.awt.event.KeyEvent;
import lombok.RequiredArgsConstructor;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
@RequiredArgsConstructor
public class KeyboardInput implements KeyboardAdapter {

  private final GamePanel gamePanel;

  @Override
  public void keyPressed(KeyEvent e) {
    gamePanel.getGameContext().keyPressed(e);
  }

  @Override
  public void keyReleased(KeyEvent e) {
    gamePanel.getGameContext().keyReleased(e);
  }
}
