package vn.nghlong3004.boom.online.client.service.impl;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import vn.nghlong3004.boom.online.client.constant.RoomConstant;
import vn.nghlong3004.boom.online.client.model.User;
import vn.nghlong3004.boom.online.client.model.room.*;
import vn.nghlong3004.boom.online.client.service.RoomService;

/**
 * Project: boom-online-client
 *
 * Simple in-memory implementation for OFFLINE mode.
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
public class InMemoryRoomServiceImpl implements RoomService {

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    @Override
    public RoomPage listRooms(int pageIndex, int pageSize) {
        // Offline flow bypasses Lobby, but we still return a stable page.
        List<Room> list = new ArrayList<>(rooms.values());
        list.sort(Comparator.comparing(Room::getId));
        return page(list, pageIndex, pageSize);
    }

    @Override
    public Room createRoom(User owner, String roomName) {
        Objects.requireNonNull(owner, "owner");
        String id = "OFF-" + UUID.randomUUID();
        String name = (roomName == null || roomName.isBlank()) ? "Offline Room" : roomName.trim();

        List<PlayerSlot> slots = new ArrayList<>(4);
        slots.add(
                PlayerSlot.builder()
                        .index(0)
                        .occupied(true)
                        .bot(false)
                        .userId(owner.getId())
                        .displayName(owner.getDisplayName())
                        .host(true)
                        .ready(true)
                        .characterIndex(0)
                        .build());

        // Add a bot in slot 1.
        slots.add(
                PlayerSlot.builder()
                        .index(1)
                        .occupied(true)
                        .bot(true)
                        .userId(-1L)
                        .displayName("Bot")
                        .host(false)
                        .ready(true)
                        .characterIndex(1)
                        .build());

        for (int i = 2; i < 4; i++) {
            slots.add(PlayerSlot.builder().index(i).occupied(false).bot(false).build());
        }

        List<ChatMessage> chat = new CopyOnWriteArrayList<>();
        chat.add(
                ChatMessage.builder()
                        .id(UUID.randomUUID().toString())
                        .type(ChatMessageType.SYSTEM)
                        .content("Phòng offline đã được tạo")
                        .createdAt(Instant.now())
                        .build());

        Room room = Room.builder()
                .id(id)
                .name(name)
                .ownerId(owner.getId())
                .ownerDisplayName(owner.getDisplayName())
                .mapIndex(0)
                .status(RoomStatus.WAITING)
                .slots(slots)
                .chat(chat)
                .build();

        rooms.put(id, room);
        return room;
    }

    @Override
    public Room joinRoom(String roomId, User user) {
        // Offline: not used.
        return getRoom(roomId);
    }

    @Override
    public Room leaveRoom(String roomId, User user) {
        // Offline: close room.
        rooms.remove(roomId);
        return null;
    }

    @Override
    public Room toggleReady(String roomId, User user) {
        // Offline: host is always ready.
        return getRoom(roomId);
    }

    @Override
    public Room changeMap(String roomId, User requester, int mapIndex) {
        Room room = requireRoom(roomId);
        if (!Objects.equals(room.getOwnerId(), requester.getId())) {
            return room;
        }
        Room updated = Room.builder()
                .id(room.getId())
                .name(room.getName())
                .ownerId(room.getOwnerId())
                .ownerDisplayName(room.getOwnerDisplayName())
                .mapIndex(normalizeMapIndex(mapIndex))
                .status(room.getStatus())
                .slots(room.getSlots())
                .chat(room.getChat())
                .build();
        rooms.put(roomId, updated);
        updated.getChat()
                .add(
                        ChatMessage.builder()
                                .id(UUID.randomUUID().toString())
                                .type(ChatMessageType.SYSTEM)
                                .content("Chủ phòng đã đổi map")
                                .createdAt(Instant.now())
                                .build());
        return updated;
    }

    @Override
    public Room changeCharacter(String roomId, User user, int characterIndex) {
        Room room = requireRoom(roomId);
        List<PlayerSlot> updatedSlots = new ArrayList<>(room.getSlots().size());

        for (PlayerSlot slot : room.getSlots()) {
            if (slot != null && slot.isOccupied() && Objects.equals(slot.getUserId(), user.getId())) {
                updatedSlots.add(
                        PlayerSlot.builder()
                                .index(slot.getIndex())
                                .occupied(true)
                                .bot(slot.isBot())
                                .userId(slot.getUserId())
                                .displayName(slot.getDisplayName())
                                .host(slot.isHost())
                                .ready(slot.isReady())
                                .characterIndex(normalizeCharacterIndex(characterIndex))
                                .build());
            } else {
                updatedSlots.add(slot);
            }
        }

        Room updated = Room.builder()
                .id(room.getId())
                .name(room.getName())
                .ownerId(room.getOwnerId())
                .ownerDisplayName(room.getOwnerDisplayName())
                .mapIndex(room.getMapIndex())
                .status(room.getStatus())
                .slots(updatedSlots)
                .chat(room.getChat())
                .build();

        rooms.put(roomId, updated);
        return updated;
    }

    @Override
    public ChatMessage sendChat(String roomId, User user, String content) {
        Room room = requireRoom(roomId);
        if (content == null || content.isBlank())
            return null;

        ChatMessage msg = ChatMessage.builder()
                .id(UUID.randomUUID().toString())
                .type(ChatMessageType.USER)
                .senderId(user != null ? user.getId() : null)
                .senderDisplayName(user != null ? user.getDisplayName() : null)
                .content(content.trim())
                .createdAt(Instant.now())
                .build();

        room.getChat().add(msg);
        return msg;
    }

    @Override
    public Room getRoom(String roomId) {
        if (roomId == null)
            return null;
        return rooms.get(roomId);
    }

    private Room requireRoom(String roomId) {
        Room room = getRoom(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room not found: " + roomId);
        }
        return room;
    }

    private RoomPage page(List<Room> rooms, int pageIndex, int pageSize) {
        int total = rooms.size();
        int start = Math.max(0, pageIndex * pageSize);
        int end = Math.min(total, start + pageSize);
        List<Room> sub = (start >= end) ? List.of() : rooms.subList(start, end);
        return RoomPage.builder()
                .rooms(new ArrayList<>(sub))
                .pageIndex(pageIndex)
                .pageSize(pageSize)
                .totalRooms(total)
                .build();
    }

    private int normalizeMapIndex(int idx) {
        int n = RoomConstant.MAP_AVATARS.length;
        if (n == 0)
            return 0;
        return Math.floorMod(idx, n);
    }

    private int normalizeCharacterIndex(int idx) {
        int n = RoomConstant.PLAYER_AVATARS.length;
        if (n == 0)
            return 0;
        return Math.floorMod(idx, n);
    }
}
