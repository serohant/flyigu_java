package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.swing.*;
import java.util.regex.Pattern;

public class kayit {
    public JFrame frame;
    private JPanel kayit;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JButton kayıtOlButton;
    private JButton geriDönButton;

    Main main = new Main();
    public static void sendEmail(String recipientEmail, String subject, String body) {
        // Gmail SMTP sunucu ayarları
        String host = "smtp.gmail.com";
        String from = "flyigu547@gmail.com";  // Buraya kendi e-posta adresinizi yazın
        String password = "nzmh ywtd estc ltrx";  // Buraya e-posta hesabınızın şifresini yazın

        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Oturum açma
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // E-posta mesajını oluştur
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject(subject);
            message.setText(body);

            // E-posta gönder
            Transport.send(message);
            System.out.println("E-posta başarıyla gönderildi.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void kayitEkle(String kullaniciAdi, String sifre, String isim, String soyisim, String email) throws SQLException {
        String DB_URL = "jdbc:mysql://localhost:3306/javapro";
        String DB_USER = "root";
        String DB_PASSWORD = "";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Kullanıcı adı veya e-posta kontrolü
            String kontrolSorgusu = "SELECT COUNT(*) FROM users WHERE username = ? OR mail = ?";
            try (PreparedStatement kontrolPreparedStatement = connection.prepareStatement(kontrolSorgusu)) {
                kontrolPreparedStatement.setString(1, kullaniciAdi);
                kontrolPreparedStatement.setString(2, email);

                try (ResultSet resultSet = kontrolPreparedStatement.executeQuery()) {
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(null, "Kullanıcı adı veya E-Posta sistemde kayıtlı lütfen tekrar deneyin.");
                        System.out.println("Kullanıcı adı veya e-posta zaten mevcut.");
                        return;
                    }
                }
            }

            // Kullanıcı kaydı ekleme
            String eklemeSorgusu = "INSERT INTO users (username, password, name, surname, mail) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(eklemeSorgusu)) {
                preparedStatement.setString(1, kullaniciAdi);
                preparedStatement.setString(2, sifre);
                preparedStatement.setString(3, isim);
                preparedStatement.setString(4, soyisim);
                preparedStatement.setString(5, email);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    sendEmail(email, "Aramıza hoşgeldin " + isim + "!", "Merhaba, " + kullaniciAdi + "! \n\nKayıt işleminin başarıyla tamamlandı! \n\nSeni aramızda gördüğümüz için çok mutluyuz!");
                } else {
                    System.out.println("Kayıt eklenirken bir hata oluştu.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Veritabanı bağlantısı sırasında bir hata oluştu.");
        }
    }

    public kayit() {
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
        frame.setContentPane(this.kayit);
        // Butonlara tıklama olaylarını ekle
        initializeButtonActions();
        // Pencereyi görünür yap
        frame.setVisible(true);

    }
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pattern.matcher(email).matches();
    }

    private void initializeButtonActions() {
        kayıtOlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textField1.getText().equals("")) {
                    if (!textField2.getText().equals("")) {
                        if (!textField3.getText().equals("") && isValidEmail(textField3.getText()) ) {
                            if (!textField4.getText().equals("")) {
                                if (!passwordField1.getText().equals("") && passwordField1.getText().length() > 5) {
                                    try {
                                        kayitEkle(textField2.getText(),passwordField1.getText(),textField1.getText(),textField4.getText(),textField3.getText());
                                        JOptionPane.showMessageDialog(null, "Kayıt işlemi başarılı! Giriş sayfasına yönlendiriliyorsunuz");
                                        frame.dispose(); // Mevcut pencereyi kapat
                                        giris form = new giris(); // Yeni formu aç
                                    } catch (SQLException ex) {
                                        ex.printStackTrace();
                                        JOptionPane.showMessageDialog(null, "Bir hata oluştu: " + ex.getMessage());
                                    }
                                }else{
                                    JOptionPane.showMessageDialog(null, "Şifre en az 6 karakterden oluşmalı");
                                }
                            }else{
                                JOptionPane.showMessageDialog(null, "Soyisim boş bırakılamaz");
                            }
                        }else{
                            JOptionPane.showMessageDialog(null, "E-Posta 'xxx@xxx.com' formatında dışında ya da boş olamaz");
                        }
                    }else{
                        JOptionPane.showMessageDialog(null, "Kullanıcı adı boş olamaz");
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "İsim boş bırakılamaz");
                }
            }
        });
        geriDönButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Son tıklanan buton: Geri dön");
                frame.dispose(); // Mevcut pencereyi kapat
                anasayfa anasayfa = new anasayfa(); // Yeni formu aç
            }
        });
    }
    
}
