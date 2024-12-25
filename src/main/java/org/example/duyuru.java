package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class duyuru {
    public JFrame frame;
    private JTextArea textArea1;
    private JPanel panel1;
    private JButton button1;
    private JButton geriDönButton;

    public duyuru() {
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
        frame.setContentPane(this.panel1);

        // Butonlara tıklama olaylarını ekle
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textArea1.getText();
                if (message == null || message.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Duyuru mesajı boş olamaz.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Kullanıcıların e-posta adreslerini çek
                List<String> emailList = fetchUserEmails();
                if (emailList.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Hiçbir e-posta adresi bulunamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Her kullanıcıya e-posta gönder
                for (String email : emailList) {
                    sendEmail(email, "Duyuru", message);
                }

                JOptionPane.showMessageDialog(frame, "Duyuru başarıyla gönderildi!", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        geriDönButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Son tıklanan buton: Kullanıcı Listele");
                frame.dispose();
                yonetici yonetici = new yonetici();
            }
        });

        // Pencereyi görünür yap
        frame.setVisible(true);
    }

    private List<String> fetchUserEmails() {
        List<String> emailList = new ArrayList<>();
        String dbUrl = "jdbc:mysql://localhost:3306/javapro";
        String dbUser = "root";
        String dbPassword = "";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement statement = connection.createStatement()) {

            String query = "SELECT mail FROM users";
            try (ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    emailList.add(resultSet.getString("mail"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Veritabanından e-posta adresleri alınamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
        }

        return emailList;
    }

    public static void sendEmail(String recipientEmail, String subject, String body) {
        // Gmail SMTP sunucu ayarları
        String host = "smtp.gmail.com";
        String from = "flyigu547@gmail.com"; // Buraya kendi e-posta adresinizi yazın
        String password = "nzmh ywtd estc ltrx"; // Buraya e-posta hesabınızın şifresini yazın

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
            // E-posta oluştur ve gönder
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("E-posta gönderildi: " + recipientEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
