package com.vn.nghlong3004.client.game;

import com.vn.nghlong3004.client.constant.GameConstant;
import com.vn.nghlong3004.client.input.KeyboardInput;
import com.vn.nghlong3004.client.input.MouseInput;
import java.awt.*;
import javax.swing.*;
import lombok.Getter;
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
public class GamePanel extends JPanel {

  @Getter private final GameAdapter gameContext;

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    gameContext.render(g);
  }

  protected void update() {
    gameContext.update();
  }

  protected void setting() {
    setInput();
    setSize();
  }

  private void setInput() {
    MouseInput mouseInput = new MouseInput(this);
    addKeyListener(new KeyboardInput(this));
    addMouseListener(mouseInput);
    addMouseWheelListener(mouseInput);
    addMouseMotionListener(mouseInput);
  }

  private void setSize() {
    Dimension size = new Dimension(GameConstant.GAME_WIDTH, GameConstant.GAME_HEIGHT);
    setMinimumSize(size);
    setPreferredSize(size);
    setMaximumSize(size);
  }
}
