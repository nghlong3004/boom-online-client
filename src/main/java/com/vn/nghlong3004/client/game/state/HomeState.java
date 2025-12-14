package com.vn.nghlong3004.client.game.state;

import com.vn.nghlong3004.client.constant.GameConstant;
import com.vn.nghlong3004.client.context.GameContext;
import java.awt.*;
import java.awt.image.BufferedImage;
import lombok.Builder;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/13/2025
 */
@Builder
public class HomeState implements GameState {

  private final BufferedImage background;

  @Override
  public void previous(GameContext gameContext) {
    gameContext.changeState(GameStateType.WELCOME);
  }

  @Override
  public void next(GameContext gameContext) {
    gameContext.changeState(GameStateType.START);
  }

  @Override
  public void print() {}

  @Override
  public void render(Graphics g) {
    g.drawImage(background, 0, 0, GameConstant.GAME_WIDTH, GameConstant.GAME_HEIGHT, null);
  }
}
