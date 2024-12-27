package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class updateInfo {
    public JFrame frame;
    private JPanel panel1;
    private JTextField textField4;
    private JButton güncelleButton;
    private JPasswordField passwordField1;
    private JPasswordField passwordField2;
    private JPasswordField passwordField3;
    private JButton geridon;

    public updateInfo() {
        // Tema ayarını yapıcı içinde ayarla
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        // JFrame oluştur ve ayarla
        frame = new JFrame("FLYIGU");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 300);
        Main main = new Main();
        Map<String, String> userdatta = main.getUserData();
        // JPanel'i JFrame'e ekle
        frame.setContentPane(this.panel1);

        // Güncelleme butonuna tıklama olayını ekle
        güncelleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String oldPassword = passwordField1.getText();
                String newPassword = passwordField2.getText();
                String newPassword2 = passwordField3.getText();

                if (oldPassword.isEmpty() && newPassword.isEmpty() && newPassword2.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Lütfen güncellemek istediğiniz alanları doldurun.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String jdbcUrl = "jdbc:mysql://localhost:3306/javapro";
                String dbUsername = "root";
                String dbPassword = "";

                try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
                    boolean isUpdated = false;

                    // Eski şifre doğrulama
                    if (!oldPassword.isEmpty() && !newPassword.isEmpty() && !newPassword2.isEmpty() && newPassword.equals(newPassword2)) {
                        if (userdatta.get("password").equals(oldPassword)) {
                            String updatePasswordQuery = "UPDATE users SET password = ? WHERE id = ?";
                            try (PreparedStatement passwordStatement = connection.prepareStatement(updatePasswordQuery)) {
                                passwordStatement.setString(1, newPassword);
                                passwordStatement.setString(2, userdatta.get("id"));
                                passwordStatement.executeUpdate();
                                isUpdated = true;
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Eski şifreniz hatalı.", "Hata", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }

                    // E-posta güncelleme

                    if (isUpdated) {
                        JOptionPane.showMessageDialog(frame, "Bilgiler başarıyla güncellendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Hiçbir bilgi güncellenmedi.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Bir hata oluştu: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Pencereyi görünür yap
        frame.setVisible(true);
        geridon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Mevcut pencereyi kapat
            }
        });
    }
}
