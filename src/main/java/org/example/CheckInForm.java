package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CheckInForm {
    private JFrame frame;
    private JPanel mainPanel;
    private int ticketId;
    private String userId;
    private String selectedSeat = null;
    private List<JToggleButton> seatButtons = new ArrayList<>();

    public CheckInForm(int ticketId, String userId) {
        this.ticketId = ticketId;
        this.userId = userId;

        // Tema ayarı
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        // Frame oluşturma
        frame = new JFrame("Check-in İşlemi");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 800);

        // Ana panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Başlık
        JLabel titleLabel = new JLabel("Check-in İşlemi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Bilet bilgileri
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        loadTicketInfo(infoPanel);
        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Koltuk seçimi paneli
        JPanel seatPanel = new JPanel();
        seatPanel.setLayout(new BoxLayout(seatPanel, BoxLayout.Y_AXIS));
        JLabel seatLabel = new JLabel("Koltuk Seçimi");
        seatLabel.setFont(new Font("Arial", Font.BOLD, 16));
        seatLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        seatPanel.add(seatLabel);
        seatPanel.add(Box.createVerticalStrut(10));

        // Uçak koltuklarını oluştur
        createSeatLayout(seatPanel);
        mainPanel.add(seatPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // QR Kod paneli
        JPanel qrPanel = new JPanel();
        qrPanel.setLayout(new BoxLayout(qrPanel, BoxLayout.Y_AXIS));
        JLabel qrLabel = new JLabel();
        qrLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        qrPanel.add(qrLabel);
        mainPanel.add(qrPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        JButton checkInButton = new JButton("Check-in Yap");
        checkInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedSeat == null) {
                    JOptionPane.showMessageDialog(frame, "Lütfen bir koltuk seçin!");
                    return;
                }
                handleCheckIn(qrLabel);
            }
        });
        buttonPanel.add(checkInButton);

        JButton backButton = new JButton("Geri Dön");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel);

        // Frame'i ayarla
        frame.setContentPane(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void createSeatLayout(JPanel seatPanel) {
        ButtonGroup seatGroup = new ButtonGroup();
        JPanel planeLayout = new JPanel(new GridLayout(10, 6, 5, 5)); // 10 sıra, her sırada 6 koltuk

        // Koltuk başlıkları
        String[] columns = {"A", "B", "C", "D", "E", "F"};
        JPanel headerPanel = new JPanel(new GridLayout(1, 6, 5, 5));
        for (String col : columns) {
            JLabel label = new JLabel(col, SwingConstants.CENTER);
            headerPanel.add(label);
        }
        seatPanel.add(headerPanel);
        seatPanel.add(Box.createVerticalStrut(10));

        // Koltukları oluştur
        for (int row = 1; row <= 10; row++) {
            for (int col = 0; col < 6; col++) {
                String seatNumber = row + columns[col];
                JToggleButton seatButton = new JToggleButton(seatNumber);
                seatButton.setPreferredSize(new Dimension(50, 50));
                
                // Koridor için boşluk bırak
                if (col == 2) {
                    planeLayout.add(Box.createHorizontalStrut(20));
                }

                seatButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        selectedSeat = seatNumber;
                    }
                });

                seatButtons.add(seatButton);
                seatGroup.add(seatButton);
                planeLayout.add(seatButton);
            }
        }

        // Dolu koltukları işaretle
        markOccupiedSeats();

        seatPanel.add(planeLayout);
    }

    private void markOccupiedSeats() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javapro", "root", "")) {
            String query = "SELECT seat_number FROM seats WHERE is_occupied = true";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String occupiedSeat = rs.getString("seat_number");
                for (JToggleButton button : seatButtons) {
                    if (button.getText().equals(occupiedSeat)) {
                        button.setEnabled(false);
                        button.setBackground(Color.RED);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleCheckIn(JLabel qrLabel) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javapro", "root", "")) {
            // Check-in durumunu kontrol et
            String checkQuery = "SELECT ischecked FROM tickets WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setInt(1, ticketId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getBoolean("ischecked")) {
                JOptionPane.showMessageDialog(frame, "Bu bilet için zaten check-in yapılmış!");
                return;
            }

            // Koltuğu kaydet
            String seatQuery = "INSERT INTO seats (ticket_id, seat_number, is_occupied) VALUES (?, ?, true)";
            PreparedStatement seatStmt = conn.prepareStatement(seatQuery);
            seatStmt.setInt(1, ticketId);
            seatStmt.setString(2, selectedSeat);
            seatStmt.executeUpdate();

            // Check-in yap
            String updateQuery = "UPDATE tickets SET ischecked = true WHERE id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setInt(1, ticketId);
            updateStmt.executeUpdate();

            // QR kod oluştur
            String qrContent = String.format("FLYIGU-TICKET-%d-%s-SEAT-%s", ticketId, userId, selectedSeat);
            BufferedImage qrImage = generateQRCode(qrContent, 200, 200);
            ImageIcon qrIcon = new ImageIcon(qrImage);
            qrLabel.setIcon(qrIcon);

            // Başarı mesajı
            JOptionPane.showMessageDialog(frame, 
                "Check-in başarıyla tamamlandı!\nSeçilen Koltuk: " + selectedSeat + "\nLütfen QR kodunuzu kaydedin veya ekran görüntüsünü alın.", 
                "Check-in Başarılı", 
                JOptionPane.INFORMATION_MESSAGE);

            // E-posta gönder
            sendCheckInEmail();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Check-in işlemi sırasında bir hata oluştu: " + e.getMessage());
        }
    }

    private void loadTicketInfo(JPanel panel) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javapro", "root", "")) {
            String query = "SELECT * FROM tickets WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, ticketId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                panel.add(new JLabel("Bilet No:"));
                panel.add(new JLabel(String.valueOf(ticketId)));
                panel.add(new JLabel("Kalkış:"));
                panel.add(new JLabel(rs.getString("departure_airport_code")));
                panel.add(new JLabel("Varış:"));
                panel.add(new JLabel(rs.getString("arrival_airport_code")));
                panel.add(new JLabel("Tarih:"));
                panel.add(new JLabel(rs.getString("departure_date")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Bilet bilgileri yüklenirken hata oluştu: " + e.getMessage());
        }
    }

    private BufferedImage generateQRCode(String content, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "QR kod oluşturulurken hata oluştu: " + e.getMessage());
            return null;
        }
    }

    private void sendCheckInEmail() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javapro", "root", "")) {
            // Kullanıcı bilgilerini al
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

                String subject = "FLYIGU - Check-in Onayı";
                String body = String.format(
                    "Sayın Yolcumuz,\n\n" +
                    "Check-in işleminiz başarıyla tamamlanmıştır.\n\n" +
                    "Bilet Detayları:\n" +
                    "Bilet No: %d\n" +
                    "Kalkış: %s\n" +
                    "Varış: %s\n" +
                    "Tarih: %s\n\n" +
                    "İyi uçuşlar dileriz.",
                    ticketId, departure, arrival, date
                );

                kayit.sendEmail(email, subject, body);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 