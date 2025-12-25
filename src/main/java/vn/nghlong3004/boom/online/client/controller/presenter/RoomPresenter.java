package vn.nghlong3004.boom.online.client.controller.presenter;

import lombok.RequiredArgsConstructor;
import vn.nghlong3004.boom.online.client.constant.RoomConstant;
import vn.nghlong3004.boom.online.client.controller.view.room.RoomPanel;
import vn.nghlong3004.boom.online.client.model.User;
import vn.nghlong3004.boom.online.client.model.room.Room;
import vn.nghlong3004.boom.online.client.service.RoomService;
import vn.nghlong3004.boom.online.client.session.UserSession;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
@RequiredArgsConstructor
public class RoomPresenter {

  private final RoomPanel view;
  private final RoomService roomService;
  private final boolean onlineMode;

  private Room currentRoom;

  public void init(Room room) {
    this.currentRoom = room;
    view.renderRoom(currentRoom);
  }

  public void onBackClicked() {
    User me = UserSession.getInstance().getCurrentUser();
    if (me != null && currentRoom != null) {
      roomService.leaveRoom(currentRoom.getId(), me);
    }
    if (onlineMode) {
      view.backToLobby();
    } else {
      view.backToHome();
    }
  }

  public void onToggleReadyClicked() {
    User me = UserSession.getInstance().getCurrentUser();
    if (me == null || currentRoom == null) return;

    currentRoom = roomService.toggleReady(currentRoom.getId(), me);
    view.renderRoom(currentRoom);
  }

  public void onStartClicked() {
    // Game start integration will be wired later.
    view.showInfoUpcoming();
  }

  public void onMapLeft() {
    changeMapBy(-1);
  }

  public void onMapRight() {
    changeMapBy(1);
  }

  private void changeMapBy(int delta) {
    User me = UserSession.getInstance().getCurrentUser();
    if (me == null || currentRoom == null) return;

    int next = Math.floorMod(currentRoom.getMapIndex() + delta, RoomConstant.MAP_AVATARS.length);
    currentRoom = roomService.changeMap(currentRoom.getId(), me, next);
    view.renderRoom(currentRoom);
  }

  public void onCharacterLeft() {
    changeCharacterBy(-1);
  }

  public void onCharacterRight() {
    changeCharacterBy(1);
  }

  private void changeCharacterBy(int delta) {
    User me = UserSession.getInstance().getCurrentUser();
    if (me == null || currentRoom == null) return;

    int current = view.getMyCharacterIndex(currentRoom, me.getId());
    int next = Math.floorMod(current + delta, RoomConstant.PLAYER_AVATARS.length);
    currentRoom = roomService.changeCharacter(currentRoom.getId(), me, next);
    view.renderRoom(currentRoom);
  }

  public void onSendChat(String content) {
    User me = UserSession.getInstance().getCurrentUser();
    if (me == null || currentRoom == null) return;

    roomService.sendChat(currentRoom.getId(), me, content);
    // Re-render chat to include new message.
    currentRoom = roomService.getRoom(currentRoom.getId());
    view.renderRoom(currentRoom);
  }
}
