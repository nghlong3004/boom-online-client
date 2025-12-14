package com.vn.nghlong3004.client.game.state;

import com.vn.nghlong3004.client.context.GameContext;
import java.awt.*;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/13/2025
 */
public class HomeState implements GameState {
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
  public void render(Graphics g) {}
}
