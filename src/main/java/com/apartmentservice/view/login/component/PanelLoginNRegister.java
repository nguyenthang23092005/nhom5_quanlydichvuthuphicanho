package com.apartmentservice.view.login.component;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */

import com.apartmentservice.controller.AuthController;
import com.apartmentservice.model.Account;
import com.apartmentservice.utils.Validator;
import com.apartmentservice.view.admin.InterfaceFrame;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;
import com.apartmentservice.view.login.swing.Button;
import com.apartmentservice.view.login.swing.MyTextField;
import com.apartmentservice.view.login.swing.MyPasswordField;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;


public class PanelLoginNRegister extends javax.swing.JLayeredPane {
    private AuthController authController = new AuthController();
    public PanelLoginNRegister() {
        initComponents();
        initRegister();
        initLogin();
        
        login.setVisible(false);
        register.setVisible(true);
    }
    
    private void initRegister()
    {
        register.setLayout(new MigLayout("wrap", "push[center]push","push[]25[]10[]10[]25[]push"));

        JLabel label = new JLabel("Tạo Tài Khoản");
        label.setFont(new Font("sansserif", 1, 30));
        label.setForeground(new Color(0,51,102));
        register.add(label);
        
        //username
        MyTextField textUser = new MyTextField();
        textUser.setPrefixIcon(new ImageIcon(getClass().getResource("/com/apartmentservice/icons/user.png")));
        
        textUser.setHint("Tên đăng nhập");
        register.add(textUser,"w 60%");
        
        //email
        MyTextField textEmail = new MyTextField();
        textEmail.setPrefixIcon(new ImageIcon(getClass().getResource("/com/apartmentservice/icons/mail.png")));
        textEmail.setHint("Email");
        register.add(textEmail,"w 60%");
        
        //password
        MyTextField textPass = new MyTextField();
        textPass.setPrefixIcon(new ImageIcon(getClass().getResource("/com/apartmentservice/icons/pass.png")));
        textPass.setHint("Mật khẩu");
        register.add(textPass,"w 60%");
        
        //sign up button
        Button cmd = new Button();
        cmd.setBackground(new Color(0,51,102));
        cmd.setForeground(new Color(250,250,250));
        cmd.setText("ĐĂNG KÝ");
        register.add(cmd, "w 40%, h 40");
        cmd.addActionListener(new ActionListener() {
        @Override
            public void actionPerformed(ActionEvent e) {
                String username = textUser.getText();
                String email = textEmail.getText();
                String password = textPass.getText();
                register(username, email, password);
            }
    });
    }

    //init login panel 
    private void initLogin()
    {
        login.setLayout(new MigLayout("wrap", "push[center]push","push[]25[]10[]10[]10[]25[]push"));
        
        //title 
        JLabel label = new JLabel("Đăng Nhập");
        label.setFont(new Font("sansserif", 1, 30));
        label.setForeground(new Color(0,51,102));
        login.add(label);
        
        //username
        MyTextField textUser = new MyTextField();
        textUser.setPrefixIcon(new ImageIcon(getClass().getResource("/com/apartmentservice/icons/user.png")));
        textUser.setHint("Tên đăng nhập");
        login.add(textUser,"w 60%");
            
        //password
        MyPasswordField textPass = new MyPasswordField();
        textPass.setPrefixIcon(new ImageIcon(getClass().getResource("/com/apartmentservice/icons/pass.png")));
        textPass.setHint("Mật khẩu");
        login.add(textPass,"w 60%");
        
        
        //sign in button
        Button cmd = new Button();
        cmd.setBackground(new Color(0,51,102));
        cmd.setForeground(new Color(250,250,250));
        cmd.setText("ĐĂNG NHẬP");
        //thêm sự kiện click vào button, khi click vào button sẽ gọi hàm login
        cmd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = textUser.getText();
                String password = new String(textPass.getPassword());
                 login(username, password);
            }
        });
        login.add(cmd, "w 40%, h 40");
    }

    // Hàm Login
    private void login(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        try {
            Account acc = authController.login(username, password);
            if (acc != null) {
                InterfaceFrame main = new InterfaceFrame(acc.getUsername());
                main.setVisible(true);
                main.setLocationRelativeTo(null);
                SwingUtilities.getWindowAncestor(this).dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập hoặc mật khẩu không đúng");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi đăng nhập.");
        }
    }
    //hàm hiển thị register panel
    public void showRegister(boolean show)
    {
        if(show)
        {
            register.setVisible(true);
            login.setVisible(false);
        }
        else
        {
            register.setVisible(false);
            login.setVisible(true);
        }
    }
    
//    hàm đăng ký
    private void register(String username, String email, String password) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (!Validator.isValidName(username)) {
            JOptionPane.showMessageDialog(this, "Tên người dùng không hợp lệ!");
            return;
        }

        if (!Validator.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ!");
            return;
        }

        if (!Validator.isSafeText(password)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu không hợp lệ!");
            return;
        }

        try {
            boolean success = authController.register(username, email, password);
            if (success) {
                JOptionPane.showMessageDialog(this, "Đăng ký thành công!");
                showRegister(false);  // Quay lại giao diện đăng nhập
            } else {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại!");
            }
        } catch (Exception e) {
            e.printStackTrace(); // để dễ debug nếu có lỗi thực thi
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi trong quá trình đăng ký.");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        login = new javax.swing.JPanel();
        register = new javax.swing.JPanel();

        setLayout(new java.awt.CardLayout());

        login.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout loginLayout = new javax.swing.GroupLayout(login);
        login.setLayout(loginLayout);
        loginLayout.setHorizontalGroup(
            loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        loginLayout.setVerticalGroup(
            loginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        add(login, "card3");

        register.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout registerLayout = new javax.swing.GroupLayout(register);
        register.setLayout(registerLayout);
        registerLayout.setHorizontalGroup(
            registerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        registerLayout.setVerticalGroup(
            registerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        add(register, "card2");
    }// </editor-fold>//GEN-END:initComponents
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel login;
    private javax.swing.JPanel register;
    // End of variables declaration//GEN-END:variables
}
