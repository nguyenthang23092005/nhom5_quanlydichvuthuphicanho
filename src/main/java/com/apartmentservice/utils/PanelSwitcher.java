package com.apartmentservice.utils;

import javax.swing.*;
import java.awt.*;

public class PanelSwitcher {
    /**
     * Chuyển đổi panel trong container (dùng BorderLayout.CENTER).
     * @param container Panel cha (ví dụ: mainPanel trong InterfaceFrame).
     * @param newPanel Panel muốn chuyển tới.
     */
    public static void switchPanel(JPanel container, JPanel newPanel) {
        Component currentPanel = null;
        for (Component comp : container.getComponents()) {
            if (comp.isVisible()) {
                currentPanel = comp;
                break;
            }
        }

        if (currentPanel != null && currentPanel.getClass().equals(newPanel.getClass())) {
            JOptionPane.showMessageDialog(container,
                    "Bạn đang ở giao diện này rồi.",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        container.removeAll();
        container.add(newPanel, BorderLayout.CENTER);
        container.revalidate();
        container.repaint();
    }
}