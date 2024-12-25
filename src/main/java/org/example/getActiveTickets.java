package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Map;

public class getActiveTickets {
    public JFrame frame;
    private JPanel panel;
    private JTable table;
    private JButton geriDönButton;
    private JButton işlemYapButton;
    private JButton iptalButton;

    public getActiveTickets() {
        // Tema ayarı
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        // Frame oluşturma
        frame = new JFrame("Aktif Biletlerim");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 400);

        // Panel oluşturma
        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Tablo oluşturma
        createTicketTable();

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        geriDönButton = new JButton("Geri Dön");
        geriDönButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new kullanici();
            }
        });
        buttonPanel.add(geriDönButton);

        işlemYapButton = new JButton("Check-in Yap");
        işlemYapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int ticketId = (int) table.getValueAt(selectedRow, 0);
                    Main main = new Main();
                    Map<String, String> userData = main.getUserData();
                    new CheckInForm(ticketId, userData.get("id"));
                } else {
                    JOptionPane.showMessageDialog(frame, "Lütfen bir bilet seçin.");
                }
            }
        });
        buttonPanel.add(işlemYapButton);

        // İptal butonu
        iptalButton = new JButton("Bileti İptal Et");
        iptalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int ticketId = (int) table.getValueAt(selectedRow, 0);
                    handleTicketCancellation(ticketId);
                } else {
                    JOptionPane.showMessageDialog(frame, "Lütfen bir bilet seçin.");
                }
            }
        });
        buttonPanel.add(iptalButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Frame'i ayarla
        frame.setContentPane(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void handleTicketCancellation(int ticketId) {
        int choice = JOptionPane.showConfirmDialog(frame,
                "Bileti iptal etmek istediğinizden emin misiniz?\nBu işlem geri alınamaz!",
                "Bilet İptali",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javapro", "root", "")) {
                // Önce bilet detaylarını al
                String getTicketQuery = "SELECT * FROM tickets WHERE id = ?";
                PreparedStatement getTicketStmt = conn.prepareStatement(getTicketQuery);
                getTicketStmt.setInt(1, ticketId);
                ResultSet ticketRs = getTicketStmt.executeQuery();

                if (ticketRs.next()) {
                    // Bileti iptal et
                    String updateQuery = "UPDATE tickets SET status = 'CANCELLED' WHERE id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setInt(1, ticketId);
                    updateStmt.executeUpdate();

                    // E-posta gönder
                    sendCancellationEmail(ticketId);

                    JOptionPane.showMessageDialog(frame,
                            "Bilet başarıyla iptal edildi.\nİptal onayı e-posta adresinize gönderilecektir.",
                            "İptal Başarılı",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Tabloyu güncelle
                    refreshTable();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                        "Bilet iptal edilirken bir hata oluştu: " + ex.getMessage(),
                        "Hata",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void sendCancellationEmail(int ticketId) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javapro", "root", "")) {
            String query = "SELECT u.mail, t.departure_airport_code, t.arrival_airport_code, t.departure_date " +
                          "FROM users u JOIN tickets t ON u.id = t.user_id " +
                          "WHERE t.id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, ticketId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String email = rs.getString("mail");
                String departure = rs.getString("departure_airport_code");
                String arrival = rs.getString("arrival_airport_code");
                String date = rs.getString("departure_date");

                String subject = "FLYIGU - Bilet İptal Bildirimi";
                String body = String.format(
                    "Sayın Yolcumuz,\n\n" +
                    "Biletiniz başarıyla iptal edilmiştir.\n\n" +
                    "İptal Edilen Bilet Detayları:\n" +
                    "Bilet No: %d\n" +
                    "Kalkış: %s\n" +
                    "Varış: %s\n" +
                    "Tarih: %s\n\n" +
                    "İade işleminiz en kısa sürede gerçekleştirilecektir.",
                    ticketId, departure, arrival, date
                );

                kayit.sendEmail(email, subject, body);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshTable() {
        // Mevcut tabloyu temizle
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        // Veritabanından güncel verileri çek
        String dbUrl = "jdbc:mysql://localhost:3306/javapro";
        String dbUser = "root";
        String dbPassword = "";
        Main main = new Main();
        Map<String, String> userData = main.getUserData();

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            String query = "SELECT * FROM tickets WHERE user_id = ? AND (status IS NULL OR status != 'CANCELLED')";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userData.get("id"));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String status = resultSet.getString("status");
                if (status == null) status = "ACTIVE";
                
                model.addRow(new Object[]{
                        resultSet.getInt("id"),
                        resultSet.getString("departure_airport_code"),
                        resultSet.getString("arrival_airport_code"),
                        resultSet.getString("departure_date"),
                        status
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Veritabanı hatası: " + e.getMessage());
        }
    }

    private void createTicketTable() {
        // Tablo modeli
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Bilet ID");
        model.addColumn("Kalkış");
        model.addColumn("Varış");
        model.addColumn("Tarih");
        model.addColumn("Durum");

        // Veritabanından biletleri çek
        String dbUrl = "jdbc:mysql://localhost:3306/javapro";
        String dbUser = "root";
        String dbPassword = "";
        Main main = new Main();
        Map<String, String> userData = main.getUserData();

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            // Önce status sütununun varlığını kontrol et
            DatabaseMetaData md = connection.getMetaData();
            ResultSet rs = md.getColumns(null, null, "tickets", "status");
            
            String query;
            if (rs.next()) {
                // status sütunu varsa
                query = "SELECT * FROM tickets WHERE user_id = ? AND (status IS NULL OR status != 'CANCELLED')";
            } else {
                // status sütunu yoksa
                query = "SELECT * FROM tickets WHERE user_id = ?";
            }
            
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userData.get("id"));
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String status;
                try {
                    status = resultSet.getString("status");
                    if (status == null) status = "ACTIVE";
                } catch (SQLException e) {
                    status = "ACTIVE";
                }
                
                model.addRow(new Object[]{
                        resultSet.getInt("id"),
                        resultSet.getString("departure_airport_code"),
                        resultSet.getString("arrival_airport_code"),
                        resultSet.getString("departure_date"),
                        status
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Veritabanı hatası: " + e.getMessage());
        }

        // Tabloyu oluştur
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
    }
}
