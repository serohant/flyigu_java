package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.util.Map;

public class getActiveTickets {
    public JFrame frame;
    private JTable table1;
    private JPanel ticket;

    public getActiveTickets() {
        // Tema ayarını yapıcı içinde ayarla
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        // JFrame oluştur ve ayarla
        frame = new JFrame("FLYIGU");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 400);

        // JPanel'i oluştur ve ayarla
        ticket = new JPanel(new BorderLayout());

        // Tablo oluştur
        table1 = new JTable();
        JScrollPane scrollPane = new JScrollPane(table1);
        ticket.add(scrollPane, BorderLayout.CENTER);

        // Buton paneli oluştur
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton geriDonButton = new JButton("Geri Dön");
        JButton checkinButton = new JButton("Check-in");
        buttonPanel.add(geriDonButton);
        buttonPanel.add(checkinButton);
        ticket.add(buttonPanel, BorderLayout.SOUTH);

        frame.setContentPane(this.ticket);

        // Veritabanından verileri tabloya ekle
        loadTickets();

        // Geri Dön butonu tıklama olayı
        geriDonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Mevcut pencereyi kapat
                kullanici k = new kullanici();
            }
        });

        // Check-in butonu tıklama olayı
        checkinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table1.getSelectedRow();
                if (selectedRow != -1) {
                    int ticketId = (int) table1.getValueAt(selectedRow, 0); // İlk sütun ticket_id
                    boolean isChecked = (boolean) table1.getValueAt(selectedRow, 6); // is_checked sütunu

                    if (!isChecked) {
                        updateCheckinStatus(ticketId);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Bu bilet zaten check-in yapılmış.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Lütfen bir satır seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Pencereyi görünür yap
        frame.setVisible(true);
    }

    private void loadTickets() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/javapro";
        String username = "root";
        String password = "";

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Bilet ID");
        model.addColumn("Kalkış Havaalanı");
        model.addColumn("Varış Havaalanı");
        model.addColumn("Kalkış Tarihi");
        model.addColumn("Varış Tarihi");
        model.addColumn("Check-in Durumu");

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM tickets WHERE departure_date > ? AND user_id = ?")) {

            preparedStatement.setDate(1, Date.valueOf(LocalDate.now()));

            Main main = new Main();
            Map<String, String> userdatta = main.getUserData();
            preparedStatement.setInt(2, Integer.parseInt(userdatta.get("id")));

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                model.addRow(new Object[]{
                        resultSet.getInt("id"),
                        resultSet.getString("departure_airport_code"),
                        resultSet.getString("arrival_airport_code"),
                        resultSet.getString("departure_date"),
                        resultSet.getString("arrival_date"),
                        resultSet.getBoolean("ischecked")
                });
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        table1.setModel(model);
    }

    private void updateCheckinStatus(int ticketId) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/javapro";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "UPDATE tickets SET ischecked = ? WHERE id = ?")) {

            preparedStatement.setBoolean(1, true);
            preparedStatement.setInt(2, ticketId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, "Check-in başarıyla yapıldı.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);

                // Tabloyu güncelle
                loadTickets();
            } else {
                JOptionPane.showMessageDialog(frame, "Check-in sırasında bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Veritabanı hatası: " + ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}
