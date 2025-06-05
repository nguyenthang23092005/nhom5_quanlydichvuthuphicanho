package com.apartmentservice.utils;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class PanelSwitcher {

    /**
     * Chuyển đổi panel trong container (dùng BorderLayout.CENTER).
     * Mỗi lần chuyển sẽ tạo mới panel từ Supplier, đảm bảo constructor chạy lại.
     * Nếu panel hiện tại cùng kiểu với panel mới, sẽ hiển thị thông báo.
     *
     * @param container Panel cha (ví dụ: mainPanel trong InterfaceFrame).
     * @param panelSupplier Supplier tạo panel mới.
     */
    public static void switchPanel(JPanel container, Supplier<JPanel> panelSupplier) {
        // Lấy panel hiện tại đang hiển thị
        Component currentPanel = null;
        for (Component comp : container.getComponents()) {
            if (comp.isVisible()) {
                currentPanel = comp;
                break;
            }
        }

        // Tạo mới panel mỗi lần chuyển từ Supplier, đảm bảo constructor chạy lại
        JPanel newPanel = panelSupplier.get();

        // Kiểm tra nếu panel hiện tại cùng kiểu với panel mới
        if (currentPanel != null && currentPanel.getClass().equals(newPanel.getClass())) {
            JOptionPane.showMessageDialog(container,
                    "Bạn đang ở giao diện này rồi.",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Cập nhật lại container với panel mới
        container.removeAll();
        container.add(newPanel, BorderLayout.CENTER);
        container.revalidate(); // Cập nhật layout
        container.repaint();    // Vẽ lại container với panel mới

        // Kiểm tra nếu panel là ReloadablePanel thì gọi reload
        if (newPanel instanceof ReloadablePanel) {
            ((ReloadablePanel) newPanel).reload();
        }
    }
}