package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ytickets {
    public JFrame frame;
    private JTable table1;
    private JButton geriDönButton;
    private JPanel tickets;

    public ytickets() {
        // Tema ayarını yapıcı içinde ayarla
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        // JPanel oluştur ve ayarla
        tickets = new JPanel();
        tickets.setLayout(new BoxLayout(tickets, BoxLayout.Y_AXIS));

        // JTable ve model oluştur
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Bilet ID");
        tableModel.addColumn("Kullanıcı ID");
        tableModel.addColumn("Kalkış Havaalanı");
        tableModel.addColumn("Varış Havaalanı");
        tableModel.addColumn("Kalkış Tarihi");
        tableModel.addColumn("Varış Tarihi");
        tableModel.addColumn("Yetişkin Sayısı");
        tableModel.addColumn("Çocuk Sayısı");
        tableModel.addColumn("Bebek Sayısı");
        tableModel.addColumn("Kabin Tipi");
        tableModel.addColumn("Check-in Durumu");
        table1 = new JTable(tableModel);

        // Veritabanından verileri çek
        String dbUrl = "jdbc:mysql://localhost:3306/javapro";
        String dbUser = "root";
        String dbPassword = "";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            String query = "SELECT * FROM tickets";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    int ticketId = resultSet.getInt("id");
                    int userId = resultSet.getInt("user_id");
                    String departureAirport = resultSet.getString("departure_airport_code");
                    String arrivalAirport = resultSet.getString("arrival_airport_code");
                    String departureDate = resultSet.getString("departure_date");
                    String arrivalDate = resultSet.getString("arrival_date");
                    int adultCount = resultSet.getInt("number_of_adults");
                    int childCount = resultSet.getInt("number_of_children");
                    int infantCount = resultSet.getInt("number_of_infants");
                    String cabinClass = resultSet.getString("cabin_class");
                    boolean isCheckedIn = resultSet.getBoolean("ischecked");

                    // Tabloya satır ekle
                    tableModel.addRow(new Object[]{
                            ticketId, userId, departureAirport, arrivalAirport, departureDate,
                            arrivalDate, adultCount, childCount, infantCount, cabinClass,
                            isCheckedIn ? "Evet" : "Hayır"
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Veritabanı hatası: " + e.getMessage());
        }

        // JScrollPane oluştur ve JTable ekle
        JScrollPane scrollPane = new JScrollPane(table1);
        tickets.add(scrollPane);

        // Geri dön butonunu oluştur ve ekle
        geriDönButton = new JButton("Geri Dön");
        geriDönButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Mevcut pencereyi kapat
                new yonetici(); // Yönetici ekranını aç
            }
        });
        tickets.add(geriDönButton);

        // JFrame oluştur ve ayarla
        frame = new JFrame("FLYIGU");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 300);

        // JPanel'i JFrame'e ekle
        frame.setContentPane(tickets);

        // Pencereyi görünür yap
        frame.setVisible(true);
    }
}
