package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class giris {
    public JFrame frame;
    private JPanel giris;
    private JButton girisYapButton;
    private JPasswordField passwordField1;
    private JTextField username;
    private JButton geriDönButton;
    private Map<String, String> userdata; // Kullanıcı verilerini saklayacak yapı
    Main main = new Main();

    public giris() {
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
        frame.setContentPane(this.giris);

        // Butonlara tıklama olaylarını ekle
        initializeButtonActions();

        // Pencereyi görünür yap
        frame.setVisible(true);
    }



    private void initializeButtonActions() {
        girisYapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputUsername = username.getText();
                String inputPassword = passwordField1.getText();

                if (inputUsername != null && !inputUsername.isEmpty() && inputPassword != null && !inputPassword.isEmpty()) {
                    // Login işlemleri
                    try {
                        String url = "jdbc:mysql://127.0.0.1:3306/javapro";
                        String dbUser = "root";
                        String dbPassword = "";

                        Connection connection = DriverManager.getConnection(url, dbUser, dbPassword);
                        System.out.println("Veritabanına bağlanıldı!");

                        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.setString(1, inputUsername);
                        statement.setString(2, inputPassword);

                        ResultSet resultSet = statement.executeQuery();

                        if (resultSet.next()) {
                            userdata = new HashMap<>();
                            ResultSetMetaData metaData = resultSet.getMetaData();
                            int columnCount = metaData.getColumnCount();

                            for (int i = 1; i <= columnCount; i++) {
                                String columnName = metaData.getColumnName(i);
                                String columnValue = resultSet.getString(columnName);
                                userdata.put(columnName, columnValue); // Populate the map
                            }
                            main.addUserData(userdata); // Pass the populated map to Main
                            System.out.println("Giriş başarılı! Kullanıcı bilgileri: " + userdata);

                            JOptionPane.showMessageDialog(null, "Giriş Başarılı.");
                            Map<String, String> userdatta = main.getUserData();
                            String userType = userdatta.get("type");
                            frame.dispose(); // Close the current frame
                            main.show(userType);

                        } else {
                            System.out.println("Giriş başarısız! Kullanıcı adı veya şifre hatalı.");
                            JOptionPane.showMessageDialog(null, "Giriş başarısız! Kullanıcı adı veya şifre hatalı.");
                        }

                        resultSet.close();
                        statement.close();
                        connection.close();

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Veritabanı hatası: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Kullanıcı adı ve şifre boş bırakılamaz.");
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
    public Map<String, String> getUserdata() {
        return userdata;
    }
}
