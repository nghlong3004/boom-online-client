package vn.nghlong3004.boom.online.client.service;

import vn.nghlong3004.boom.online.client.model.User;
import vn.nghlong3004.boom.online.client.model.room.ChatMessage;
import vn.nghlong3004.boom.online.client.model.room.Room;
import vn.nghlong3004.boom.online.client.model.room.RoomPage;

/**
 * Project: boom-online-client
 *
 * <p>Offline: InMemoryRoomServiceImpl Online: RoomServiceImpl
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
public interface RoomService {

  RoomPage listRooms(int pageIndex, int pageSize);

  Room createRoom(User owner, String roomName);

  Room joinRoom(String roomId, User user);

  Room leaveRoom(String roomId, User user);

  Room toggleReady(String roomId, User user);

  Room changeMap(String roomId, User requester, int mapIndex);

  Room changeCharacter(String roomId, User user, int characterIndex);

  ChatMessage sendChat(String roomId, User user, String content);

  Room getRoom(String roomId);
}
