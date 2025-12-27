package vn.nghlong3004.boom.online.client.controller.presenter;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.swing.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.constant.RoomConstant;
import vn.nghlong3004.boom.online.client.controller.view.room.RoomPanel;
import vn.nghlong3004.boom.online.client.model.User;
import vn.nghlong3004.boom.online.client.model.room.Room;
import vn.nghlong3004.boom.online.client.service.RoomService;
import vn.nghlong3004.boom.online.client.service.WebSocketService;
import vn.nghlong3004.boom.online.client.session.ApplicationSession;
import vn.nghlong3004.boom.online.client.session.UserSession;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
@Slf4j
@RequiredArgsConstructor
public class RoomPresenter {

  private final RoomPanel view;
  private final RoomService roomService;
  private final WebSocketService webSocketService;

  private Room currentRoom;

  public void update(Room room) {
    this.currentRoom = room;
    this.roomService.setCurrentRoom(room);
    view.renderRoom(currentRoom);
  }

  private void executeAction(CompletableFuture<Room> action) {
    action
        .thenAccept(room -> SwingUtilities.invokeLater(() -> update(room)))
        .exceptionally(
            ex -> {
              log.error("Action failed", ex);
              return null;
            });
  }

  public void onMapLeft() {
    int next = Math.floorMod(currentRoom.getMapIndex() - 1, RoomConstant.MAP_AVATARS.length);
    executeAction(roomService.changeMap(next));
  }

  public void onMapRight() {
    int next = Math.floorMod(currentRoom.getMapIndex() + 1, RoomConstant.MAP_AVATARS.length);
    executeAction(roomService.changeMap(next));
  }

  public void onCharacterLeft() {
    changeCharacterBy(-1);
  }

  public void onCharacterRight() {
    changeCharacterBy(1);
  }

  private void changeCharacterBy(int delta) {
    int myIndex =
        view.getMyCharacterIndex(currentRoom, UserSession.getInstance().getCurrentUser().getId());
    int next = Math.floorMod(myIndex + delta, RoomConstant.PLAYER_AVATARS.length);
    executeAction(roomService.changeCharacter(next));
  }

  public void onSendChat(String content) {
    if (content.isBlank()) return;
    executeAction(roomService.sendChat(content));
  }

  public void onBackClicked() {
    if (!ApplicationSession.getInstance().isOfflineMode()) {
      webSocketService.disconnect();
      view.backToLobby();
    } else {
      view.backToHome();
    }
  }

  public void onStartClicked() {
    User currentUser = UserSession.getInstance().getCurrentUser();
    if (currentRoom != null && !Objects.equals(currentRoom.getOwnerId(), currentUser.getId())) {
      return;
    }

    roomService
        .startGame()
        .thenAccept(
            room -> {
              if (ApplicationSession.getInstance().isOfflineMode()) {
                log.info("Chuyển cảnh vào trận đấu Offline...");
              }
            })
        .exceptionally(
            ex -> {
              log.error("Failed to start game", ex);
              return null;
            });
  }

  public void onToggleReadyClicked() {
    executeAction(roomService.toggleReady());
  }
}
