package vn.nghlong3004.boom.online.client.model.room;

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
public class PlayerSlot {
    private final int index;

    private final boolean occupied;
    private final boolean bot;

    private final Long userId;
    private final String displayName;

    private final boolean host;
    private final boolean ready;

    private final int characterIndex;
}
