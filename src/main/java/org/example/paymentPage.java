package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Map;

public class paymentPage {
    public JFrame frame;
    private JPanel odeme;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JButton ödemeyiTamamlaButton;
    private JTextArea textArea1;

    private Map<String, String> userdatta; // Store the passed user data

    public paymentPage(String SelectedRow, Map<String, Object> data, Map<String, String> userdata) {
        this.userdatta = userdata; // Assign passed user data
        System.out.println(SelectedRow.getClass().getName());
        String jdbcUrl = "jdbc:mysql://localhost:3306/javapro"; // Replace 'localhost:3306' and 'javapro' with your DB details
        String username = "root"; // Replace with your MySQL username
        String password = "";

        // Theme settings
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }
        updateFlightDetails(data.get("departure_airport").toString(),data.get("arrival_airport").toString(),data.get("departure_date").toString(),SelectedRow.toString());
        // JFrame setup
        frame = new JFrame("FLYIGU");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 300);
        frame.setContentPane(this.odeme);
        frame.setVisible(true);

        ödemeyiTamamlaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(textField1.getText() == null || textField2.getText() == null || textField3.getText() == null || textField4.getText() == null) {
                    JOptionPane.showMessageDialog(frame, "Lütfen kutucukları boş bırakmayın");
                }else{
                    String insertQuery = "INSERT INTO tickets (user_id, departure_airport_code, arrival_airport_code, departure_date, arrival_date, number_of_adults, number_of_children, number_of_infants, cabin_class) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
                         PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

                        // Set values for the placeholders
                        preparedStatement.setInt(1, Integer.valueOf(userdatta.get("id"))); // Use the passed user data
                        preparedStatement.setString(2, data.get("departure_airport").toString());
                        preparedStatement.setString(3, data.get("arrival_airport").toString());
                        preparedStatement.setString(4, data.get("departure_date").toString());
                        preparedStatement.setString(5, data.get("return_date").toString());
                        preparedStatement.setInt(6, Integer.valueOf(data.get("adult_count").toString()));
                        preparedStatement.setInt(7, Integer.valueOf(data.get("child_count").toString()));
                        preparedStatement.setInt(8, Integer.valueOf(data.get("baby_count").toString()));
                        preparedStatement.setString(9, data.get("cabin_type").toString());

                        // Execute the insert operation
                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            System.out.println("Ödeme başarıyla tamamlandı");
                            JOptionPane.showMessageDialog(frame, "Ödeme başarıyla tamamlandı");
                            frame.dispose();
                        } else {
                            System.out.println("Failed to insert ticket into the database.");
                            JOptionPane.showMessageDialog(frame, "Ödeme tamamlanamadı");
                        }
                    } catch (Exception d) {
                        d.printStackTrace();
                    }
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