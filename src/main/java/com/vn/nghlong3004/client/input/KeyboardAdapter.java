package com.vn.nghlong3004.client.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
public interface KeyboardAdapter extends KeyListener {
  default void keyTyped(KeyEvent e) {}
}
