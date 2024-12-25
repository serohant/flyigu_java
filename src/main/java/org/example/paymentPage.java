package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

public class paymentPage {
    public JFrame frame;
    private JPanel odeme;
    private JTextField cardNumberField;
    private JTextField expiryDateField;
    private JTextField cvvField;
    private JTextField cardHolderField;
    private JButton ödemeyiTamamlaButton;
    private JTextArea textArea1;
    private JTextField promoCodeField;
    private JButton applyPromoButton;
    private JLabel discountLabel;
    private double originalAmount;
    private double discountedAmount;

    private Map<String, String> userdatta;

    public paymentPage(String SelectedRow, Map<String, Object> data, Map<String, String> userdata) {
        this.userdatta = userdata;
        this.originalAmount = Double.parseDouble(SelectedRow);
        this.discountedAmount = this.originalAmount;
        
        String jdbcUrl = "jdbc:mysql://localhost:3306/javapro";
        String username = "root";
        String password = "";

        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        frame = new JFrame("FLYIGU");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 400);
        
        // Ana panel güncelleme
        odeme = new JPanel();
        odeme.setLayout(new BoxLayout(odeme, BoxLayout.Y_AXIS));
        
        // Bilet detayları
        textArea1 = new JTextArea();
        textArea1.setEditable(false);
        updateFlightDetails(data.get("departure_airport").toString(),
                          data.get("arrival_airport").toString(),
                          data.get("departure_date").toString(),
                          SelectedRow);
        
        // Kart bilgileri paneli
        JPanel cardPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        cardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Kart numarası
        cardPanel.add(new JLabel("Kart Numarası:"));
        cardNumberField = new JTextField(20);
        cardPanel.add(cardNumberField);
        
        // Son kullanma tarihi
        cardPanel.add(new JLabel("Son Kullanma Tarihi:"));
        expiryDateField = new JTextField(20);
        cardPanel.add(expiryDateField);
        
        // CVV
        cardPanel.add(new JLabel("CVV:"));
        cvvField = new JTextField(20);
        cardPanel.add(cvvField);
        
        // Kart sahibi
        cardPanel.add(new JLabel("Kart Sahibi:"));
        cardHolderField = new JTextField(20);
        cardPanel.add(cardHolderField);
        
        // Promosyon kodu paneli
        JPanel promoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        promoCodeField = new JTextField(15);
        applyPromoButton = new JButton("Promosyon Kodu Uygula");
        discountLabel = new JLabel("");
        
        promoPanel.add(new JLabel("Promosyon Kodu:"));
        promoPanel.add(promoCodeField);
        promoPanel.add(applyPromoButton);
        promoPanel.add(discountLabel);
        
        // Buton paneli
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ödemeyiTamamlaButton = new JButton("Ödemeyi Tamamla");
        buttonPanel.add(ödemeyiTamamlaButton);
        
        // Ana panele ekle
        odeme.add(new JScrollPane(textArea1));
        odeme.add(cardPanel);
        odeme.add(promoPanel);
        odeme.add(buttonPanel);
        
        frame.setContentPane(odeme);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Promosyon kodu uygulama butonu işleyicisi
        applyPromoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String promoCode = promoCodeField.getText().trim();
                if (promoCode.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Lütfen bir promosyon kodu girin.");
                    return;
                }
                
                try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password)) {
                    String query = "SELECT * FROM promotion_codes WHERE code = ? AND is_active = true " +
                                 "AND (expiry_date IS NULL OR expiry_date >= CURRENT_DATE) " +
                                 "AND (usage_limit IS NULL OR usage_count < usage_limit)";
                    
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, promoCode);
                    ResultSet rs = stmt.executeQuery();
                    
                    if (rs.next()) {
                        int discountPercentage = rs.getInt("discount_percentage");
                        double minAmount = rs.getDouble("min_purchase_amount");
                        
                        if (originalAmount >= minAmount) {
                            discountedAmount = originalAmount * (1 - (discountPercentage / 100.0));
                            discountLabel.setText(String.format("İndirim: %%%d - Yeni Tutar: %.2f TRY", 
                                                              discountPercentage, discountedAmount));
                            updateFlightDetails(data.get("departure_airport").toString(),
                                             data.get("arrival_airport").toString(),
                                             data.get("departure_date").toString(),
                                             String.valueOf(discountedAmount));
                            
                            // Promosyon kodu kullanım sayısını güncelle
                            String updateQuery = "UPDATE promotion_codes SET usage_count = usage_count + 1 WHERE code = ?";
                            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                            updateStmt.setString(1, promoCode);
                            updateStmt.executeUpdate();
                        } else {
                            JOptionPane.showMessageDialog(frame, 
                                String.format("Bu promosyon kodu minimum %.2f TRY tutarında alışveriş için geçerlidir.", minAmount));
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Geçersiz veya süresi dolmuş promosyon kodu!");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Promosyon kodu uygulanırken bir hata oluştu.");
                }
            }
        });

        ödemeyiTamamlaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(cardNumberField.getText().isEmpty() || 
                   expiryDateField.getText().isEmpty() || 
                   cvvField.getText().isEmpty() || 
                   cardHolderField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Lütfen tüm kart bilgilerini girin");
                    return;
                }

                String insertQuery = "INSERT INTO tickets (user_id, departure_airport_code, arrival_airport_code, " +
                                   "departure_date, arrival_date, number_of_adults, number_of_children, " +
                                   "number_of_infants, cabin_class, final_price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
                     PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

                    preparedStatement.setInt(1, Integer.valueOf(userdatta.get("id")));
                    preparedStatement.setString(2, data.get("departure_airport").toString());
                    preparedStatement.setString(3, data.get("arrival_airport").toString());
                    preparedStatement.setString(4, data.get("departure_date").toString());
                    preparedStatement.setString(5, data.get("return_date").toString());
                    preparedStatement.setInt(6, Integer.valueOf(data.get("adult_count").toString()));
                    preparedStatement.setInt(7, Integer.valueOf(data.get("child_count").toString()));
                    preparedStatement.setInt(8, Integer.valueOf(data.get("baby_count").toString()));
                    preparedStatement.setString(9, data.get("cabin_type").toString());
                    preparedStatement.setDouble(10, discountedAmount);

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(frame, "Ödeme başarıyla tamamlandı");
                        frame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Ödeme tamamlanamadı");
                    }
                } catch (Exception d) {
                    d.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Ödeme işlemi sırasında bir hata oluştu");
                }
            }
        });
    }

    private void updateFlightDetails(String departureAirport, String arrivalAirport, String departureTime, String price) {
        String details = String.format(
                "Kalkış Havaalanı: %s\nİniş Havaalanı: %s\nKalkış Saati: %s\nÜcret: %s TRY",
                departureAirport, arrivalAirport, departureTime, price
        );
        textArea1.setText(details);
    }
}