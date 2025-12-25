package vn.nghlong3004.boom.online.client.controller.presenter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javax.swing.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.nghlong3004.boom.online.client.controller.view.lobby.LobbyPanel;
import vn.nghlong3004.boom.online.client.model.User;
import vn.nghlong3004.boom.online.client.model.response.ErrorResponse;
import vn.nghlong3004.boom.online.client.service.RoomService;
import vn.nghlong3004.boom.online.client.session.UserSession;
import vn.nghlong3004.boom.online.client.util.I18NUtil;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
@Slf4j
@RequiredArgsConstructor
public class LobbyPresenter {

  private static final int PAGE_SIZE = 3;

  private final LobbyPanel view;
  private final RoomService roomService;
  private final Gson gson;

  private int pageIndex;

  public void init() {
    loadPage(0);
  }

  public void onRefreshClicked() {
    view.showInfo("common.loading");
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
    view.showInfo("common.processing");
    String defaultName =
        I18NUtil.getString("room.base_name").formatted(currentUser.getDisplayName());

    roomService
        .createRoom(currentUser, defaultName)
        .thenAccept(
            newRoom -> {
              SwingUtilities.invokeLater(
                  () -> {
                    view.showSuccess("room.create.success");
                    view.openRoom(newRoom);
                  });
            })
        .exceptionally(
            ex -> {
              SwingUtilities.invokeLater(
                  () -> {
                    log.error("Error create room: ", ex);
                    String msg =
                        ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    view.showRawError(msg);
                  });
              return null;
            });
  }

  public void onRoomSelected(String roomId) {
    User currentUser = UserSession.getInstance().getCurrentUser();
    if (currentUser == null) return;
    roomService
        .joinRoom(roomId, currentUser)
        .thenAccept(
            room -> {
              SwingUtilities.invokeLater(
                  () -> {
                    view.showSuccess("room.join.success");
                    view.openRoom(room);
                  });
            })
        .exceptionally(
            ex -> {
              handleError(ex);
              return null;
            });
  }

  private void loadPage(int targetPage) {
    if (targetPage < 0) targetPage = 0;
    final int finalPage = targetPage;

    roomService
        .rooms(finalPage, PAGE_SIZE)
        .thenAccept(
            pageResponse -> {
              SwingUtilities.invokeLater(
                  () -> {
                    int totalPages = pageResponse.getTotalPages();
                    if (totalPages > 0 && finalPage >= totalPages) {
                      loadPage(totalPages - 1);
                    } else {
                      this.pageIndex = pageResponse.getPageIndex();
                      view.render(pageResponse);
                    }
                    view.showSuccess("room.refresh");
                  });
            })
        .exceptionally(
            ex -> {
              log.error("Error loading rooms", ex);
              return null;
            });
  }

  private void handleError(Throwable ex) {
    SwingUtilities.invokeLater(
        () -> {
          log.error("Action failed: ", ex);
          Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
          String errorBody = cause.getMessage();

          // Map từ JSON Error -> Key I18N
          String messageKey = mapErrorToMessageKey(errorBody);

          if (messageKey.startsWith("Raw:")) {
            // Nếu không map được key, hiển thị raw message (bỏ tiền tố "Raw:")
            view.showRawError(messageKey.substring(4));
          } else {
            // Nếu map được key, hiển thị theo ngôn ngữ
            view.showError(messageKey);
          }
        });
  }

  private String mapErrorToMessageKey(String errorBody) {
    if (errorBody == null) return "common.error.server";

    if (errorBody.contains("ConnectException") || errorBody.contains("Network")) {
      return "common.error.server";
    }

    try {
      ErrorResponse errorResponse = gson.fromJson(errorBody, ErrorResponse.class);

      if (errorResponse != null && errorResponse.code() != null) {
        return switch (errorResponse.code()) {
          case "RoomFull" -> "room.join.full";
          case "RoomPlaying" -> "room.join.playing";
          case "RoomNotFound" -> "room.join.not_found";
          case "InvalidRequest" -> "common.error.invalid_request";
          default -> "Raw:" + errorResponse.message();
        };
      }
    } catch (JsonSyntaxException ignored) {
      log.warn("Cannot parse error body as JSON: {}", errorBody);
    }

    return "Raw:" + errorBody;
  }
}
