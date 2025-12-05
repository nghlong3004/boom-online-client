package com.vn.nghlong3004.client.view;

import javax.swing.*;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 12/6/2025
 */
public class FormPanel extends JPanel {

  private LookAndFeel oldTheme = UIManager.getLookAndFeel();

  public FormPanel() {
    init();
  }

  private void init() {}

  public void formInit() {}

  public void formOpen() {}

  public void formRefresh() {}

  protected final void formCheck() {
    if (oldTheme != UIManager.getLookAndFeel()) {
      oldTheme = UIManager.getLookAndFeel();
      SwingUtilities.updateComponentTreeUI(this);
    }
  }
}
