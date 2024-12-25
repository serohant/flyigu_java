package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Properties;
import java.util.Random;

public class anasayfa {
    private JFrame frame;
    private JPanel anasayfa;
    private JButton girişYapButton;
    private JButton kayıtOlButton;
    private JButton şifremiUnuttumButton;
    private JButton hakkimizdaButton;

    public anasayfa() {
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

        // JPanel'i JFrame'e ekle
        frame.setContentPane(this.anasayfa);

        // Butonlara tıklama olaylarını ekle
        // Pencereyi görünür yap
        frame.setVisible(true);

        girişYapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                giris giris = new giris();
            }
        });

        kayıtOlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                kayit kayit = new kayit();
            }
        });

        şifremiUnuttumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPasswordResetDialog();
            }
        });
        hakkimizdaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new hakkimizda();
            }
        });
    }

    private void showPasswordResetDialog() {
        JDialog dialog = new JDialog(frame, "Şifre Sıfırlama", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 150);

        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField emailField = new JTextField();
        panel.add(new JLabel("E-posta adresinizi girin:"));
        panel.add(emailField);

        JButton resetButton = new JButton("Şifremi Sıfırla");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText().trim();
                if (email.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Lütfen e-posta adresinizi girin.");
                    return;
                }

                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javapro", "root", "")) {
                    // E-posta adresinin veritabanında olup olmadığını kontrol et
                    String query = "SELECT id FROM users WHERE mail = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, email);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        // Yeni şifre oluştur
                        String newPassword = generateRandomPassword();
                        
                        // Şifreyi veritabanında güncelle
                        String updateQuery = "UPDATE users SET password = ? WHERE mail = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                        updateStmt.setString(1, newPassword);
                        updateStmt.setString(2, email);
                        updateStmt.executeUpdate();

                        // Yeni şifreyi e-posta ile gönder
                        sendPasswordResetEmail(email, newPassword);
                        
                        JOptionPane.showMessageDialog(dialog, 
                            "Yeni şifreniz e-posta adresinize gönderildi.\nLütfen e-postanızı kontrol edin.");
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                            "Bu e-posta adresi sistemde kayıtlı değil.", 
                            "Hata", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, 
                        "Şifre sıfırlama işlemi sırasında bir hata oluştu: " + ex.getMessage());
                }
            }
        });

        panel.add(resetButton);
        dialog.add(panel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private void sendPasswordResetEmail(String toEmail, String newPassword) {
        // E-posta gönderme ayarları
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // E-posta hesap bilgileri
        final String username = "flyigu547@gmail.com"; // Gmail adresiniz
        final String password = "nzmh ywtd estc ltrx"; // Gmail uygulama şifreniz

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("FLYIGU - Şifre Sıfırlama");
            message.setText("Merhaba,\n\n" +
                          "Şifre sıfırlama talebiniz üzerine yeni şifreniz oluşturuldu.\n\n" +
                          "Yeni şifreniz: " + newPassword + "\n\n" +
                          "Güvenliğiniz için lütfen giriş yaptıktan sonra şifrenizi değiştirin.\n\n" +
                          "İyi uçuşlar dileriz,\n" +
                          "FLYIGU Ekibi");

            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, 
                "E-posta gönderimi sırasında bir hata oluştu. Lütfen daha sonra tekrar deneyin.");
        }
    }
}