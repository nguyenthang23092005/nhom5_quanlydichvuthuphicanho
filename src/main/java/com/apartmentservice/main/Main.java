package com.apartmentservice.main;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import com.apartmentservice.view.login.LoginFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        setLookAndFeel();
        launchApplication();
    }

    private static void setLookAndFeel() {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equalsIgnoreCase(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    System.out.println("Look and Feel set to: Nimbus");
                    return;
                }
            }
            System.out.println("Nimbus Look and Feel not found. Using default.");
        } catch (Exception ex) {
            System.err.println("Failed to set Look and Feel.");
            ex.printStackTrace();
        }
    }

    private static void launchApplication() {
        System.out.println("Launching Apartment Service Management Application...");
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}