package com.vn.nghlong3004.client.input;

import java.awt.event.*;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
public interface MouseAdapter extends MouseListener, MouseWheelListener, MouseMotionListener {
  default void mouseEntered(MouseEvent e) {}

  default void mouseExited(MouseEvent e) {}

  default void mouseWheelMoved(MouseWheelEvent e) {}
}
