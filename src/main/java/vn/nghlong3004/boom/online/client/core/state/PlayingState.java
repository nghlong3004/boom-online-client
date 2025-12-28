package vn.nghlong3004.boom.online.client.core.state;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import raven.modal.ModalDialog;
import vn.nghlong3004.boom.online.client.core.BombManager;
import vn.nghlong3004.boom.online.client.core.BomberManager;
import vn.nghlong3004.boom.online.client.core.GameContext;
import vn.nghlong3004.boom.online.client.model.map.GameMap;
import vn.nghlong3004.boom.online.client.model.playing.PlayerInfo;
import vn.nghlong3004.boom.online.client.renderer.HudRenderer;
import vn.nghlong3004.boom.online.client.renderer.MapRenderer;
import vn.nghlong3004.boom.online.client.session.ApplicationSession;
import vn.nghlong3004.boom.online.client.session.PlayingSession;
import vn.nghlong3004.boom.online.client.session.UserSession;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/28/2025
 */
@Slf4j
public class PlayingState implements GameState {

  private final MapRenderer mapRenderer;
  private final HudRenderer hudRenderer;
  private BomberManager bomberManager;
  private BombManager bombManager;
  private boolean initialized;

  public PlayingState() {
    this.mapRenderer = new MapRenderer();
    this.hudRenderer = new HudRenderer();
    this.initialized = false;
  }

  @Override
  public void next(GameContext gameContext) {
    gameContext.changeState(GameStateType.PLAYING);
  }

  @Override
  public void previous(GameContext gameContext) {
    PlayingSession.getInstance().clear();
    bomberManager = null;
    bombManager = null;
    initialized = false;
    gameContext.changeState(GameStateType.START);
  }

  @Override
  public void update() {
    if (!initialized) {
      initialize();
    }

    if (bomberManager != null) {
      bomberManager.update();
    }

    if (bombManager != null) {
      bombManager.update();
      checkExplosionCollisions();
    }
  }

  private void initialize() {
    initialized = true;
    closeAllModals();
    initializeManagers();
    log.info("Game started - Map: {}", getGameMap().getMapType().getName());
  }

  private void initializeManagers() {
    GameMap gameMap = getGameMap();
    List<PlayerInfo> players = getPlayers();
    String localUserId = getCurrentUserId();

    bomberManager = new BomberManager(gameMap);
    bomberManager.initializeBombers(players, localUserId);

    bombManager = new BombManager(gameMap);
  }

  private void checkExplosionCollisions() {
    if (bomberManager == null || bombManager == null) {
      return;
    }

    bomberManager.getBombers().forEach(bomber -> {
      if (bomber.isAlive() && bombManager.isExplosionAt(bomber.getTileX(), bomber.getTileY())) {
        bomber.die();
      }
    });
  }

  private String getCurrentUserId() {
    if (UserSession.getInstance().getCurrentUser() != null) {
      return String.valueOf(UserSession.getInstance().getCurrentUser().getId());
    }
    return "offline-player";
  }

  @Override
  public void render(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    renderMap(g2d);
    renderBombs(g2d);
    renderBombers(g2d);
    renderHud(g);
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
      previous(GameContext.getInstance());
      return;
    }

    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
      handlePlaceBomb();
      return;
    }

    if (bomberManager != null) {
      bomberManager.keyPressed(e);
    }
  }

  private void handlePlaceBomb() {
    if (bomberManager != null && bombManager != null) {
      var localBomber = bomberManager.getLocalBomber();
      if (localBomber != null && localBomber.isAlive()) {
        bombManager.placeBomb(localBomber);
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    if (bomberManager != null) {
      bomberManager.keyReleased(e);
    }
  }

  private void renderMap(Graphics2D g2d) {
    GameMap gameMap = getGameMap();
    if (gameMap != null) {
      mapRenderer.render(g2d, gameMap);
    }
  }

  private void renderBombs(Graphics2D g2d) {
    if (bombManager != null) {
      bombManager.render(g2d);
    }
  }

  private void renderBombers(Graphics2D g2d) {
    if (bomberManager != null) {
      bomberManager.render(g2d);
    }
  }

  private void renderHud(Graphics g) {
    List<PlayerInfo> players = getPlayers();
    hudRenderer.render(g, players);
  }

  private GameMap getGameMap() {
    return PlayingSession.getInstance().getGameMap();
  }

  private List<PlayerInfo> getPlayers() {
    return PlayingSession.getInstance().getPlayers();
  }

  private void closeAllModals() {
    String startId = ApplicationSession.getInstance().getStartId();
    if (ModalDialog.isIdExist(startId)) {
      ModalDialog.closeModal(startId);
    }
  }
}
