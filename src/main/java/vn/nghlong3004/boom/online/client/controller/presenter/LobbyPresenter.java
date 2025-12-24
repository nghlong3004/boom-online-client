package vn.nghlong3004.boom.online.client.controller.presenter;

import lombok.RequiredArgsConstructor;
import vn.nghlong3004.boom.online.client.controller.view.lobby.LobbyPanel;
import vn.nghlong3004.boom.online.client.model.User;
import vn.nghlong3004.boom.online.client.model.room.Room;
import vn.nghlong3004.boom.online.client.model.room.RoomPage;
import vn.nghlong3004.boom.online.client.service.RoomService;
import vn.nghlong3004.boom.online.client.session.UserSession;
import vn.nghlong3004.boom.online.client.util.I18NUtil;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
@RequiredArgsConstructor
public class LobbyPresenter {

  private static final int PAGE_SIZE = 3;

  private final LobbyPanel view;
  private final RoomService roomService;

  private int pageIndex;

  public void init() {
    loadPage(0);
  }

  public void onRefreshClicked() {
    loadPage(pageIndex);
  }

  public void onPrevClicked() {
    if (pageIndex <= 0) return;
    loadPage(pageIndex - 1);
  }

  public void onNextClicked() {
    loadPage(pageIndex + 1);
  }

  public void onCreateRoomClicked() {
    User currentUser = UserSession.getInstance().getCurrentUser();
    if (currentUser == null) return;

    Room room =
        roomService.createRoom(
            currentUser,
            I18NUtil.getString("room.base_name").formatted(currentUser.getDisplayName()));
    view.openRoom(room);
  }

  public void onRoomSelected(String roomId) {
    User currentUser = UserSession.getInstance().getCurrentUser();
    if (currentUser == null) return;

    Room room = roomService.joinRoom(roomId, currentUser);
    if (room != null) {
      view.openRoom(room);
    }
  }

  private void loadPage(int newPageIndex) {
    if (newPageIndex < 0) newPageIndex = 0;

    RoomPage page = roomService.listRooms(newPageIndex, PAGE_SIZE);
    int totalPages = page.getTotalPages();
    if (totalPages > 0 && newPageIndex >= totalPages) {
      page = roomService.listRooms(totalPages - 1, PAGE_SIZE);
      newPageIndex = page.getPageIndex();
    }

    this.pageIndex = newPageIndex;
    view.render(page);
  }
}
