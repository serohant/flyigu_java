package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ykullanicilistesi {
    public static JFrame frame;
    private JPanel ykullanicilistesi;
    private JTable table1;
    private JButton geriDönButton;
    private JButton kullaniciTipiniDegistirButton;

    public ykullanicilistesi() {
        // Tema ayarını yapıcı içinde ayarla
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        // JPanel ayarla
        ykullanicilistesi = new JPanel();
        ykullanicilistesi.setLayout(new BoxLayout(ykullanicilistesi, BoxLayout.Y_AXIS));

        // JTable ve model oluştur
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Ad");
        tableModel.addColumn("Soyad");
        tableModel.addColumn("Kullanıcı Adı");
        tableModel.addColumn("E-posta");
        tableModel.addColumn("Tip");
        table1 = new JTable(tableModel);

        // Veritabanından verileri çek
        String dbUrl = "jdbc:mysql://localhost:3306/javapro";
        String dbUser = "root";
        String dbPassword = "";

        loadUserData(tableModel, dbUrl, dbUser, dbPassword);

        // JScrollPane oluştur ve JTable ekle
        JScrollPane scrollPane = new JScrollPane(table1);
        ykullanicilistesi.add(scrollPane);

        // Geri dön butonunu oluştur ve panele ekle
        geriDönButton = new JButton("Geri Dön");
        geriDönButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Mevcut pencereyi kapat
                new yonetici(); // Yeni formu aç
            }
        });

        kullaniciTipiniDegistirButton = new JButton("Kullanıcı Tipini Değiştir");
        kullaniciTipiniDegistirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table1.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(frame, "Lütfen bir kullanıcı seçin.");
                    return;
                }

                int userId = (int) tableModel.getValueAt(selectedRow, 0);
                int currentType = Integer.parseInt(tableModel.getValueAt(selectedRow, 5).toString());
                int newType = currentType == 1 ? 0 : 1;

                try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                    String updateQuery = "UPDATE users SET type = ? WHERE id = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                        preparedStatement.setInt(1, newType);
                        preparedStatement.setInt(2, userId);

                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(frame, "Kullanıcı tipi başarıyla güncellendi.");
                            tableModel.setValueAt(newType, selectedRow, 5); // Tabloyu güncelle
                        } else {
                            JOptionPane.showMessageDialog(frame, "Kullanıcı tipi güncellenemedi.");
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Veritabanı hatası: " + ex.getMessage());
                }
            }
        });

        ykullanicilistesi.add(geriDönButton);
        ykullanicilistesi.add(kullaniciTipiniDegistirButton);

        // JFrame oluştur ve ayarla
        frame = new JFrame("FLYIGU");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 400);

        // JPanel'i JFrame'e ekle
        frame.setContentPane(ykullanicilistesi);

        // Pencereyi görünür yap
        frame.setVisible(true);
    }

    private void loadUserData(DefaultTableModel tableModel, String dbUrl, String dbUser, String dbPassword) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            String query = "SELECT id, name, surname, username, mail, type FROM users";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String surname = resultSet.getString("surname");
                    String username = resultSet.getString("username");
                    String email = resultSet.getString("mail");
                    int type = resultSet.getInt("type");

                    // Tabloya satır ekle
                    tableModel.addRow(new Object[]{id, name, surname, username, email, type});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Veritabanı hatası: " + e.getMessage());
        }
    }
}