package vn.nghlong3004.boom.online.client.model.room;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/18/2025
 */
@Getter
@Builder
public class RoomPage {
    private final List<Room> rooms;
    private final int pageIndex;
    private final int pageSize;
    private final int totalRooms;

    public int getTotalPages() {
        if (pageSize <= 0)
            return 0;
        return (int) Math.ceil(totalRooms / (double) pageSize);
    }
}
