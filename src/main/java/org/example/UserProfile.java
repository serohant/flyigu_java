package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Map;

public class UserProfile {
    private JFrame frame;
    private Map<String, String> userData;
    private JLabel membershipLevelLabel;
    private JProgressBar pointsProgressBar;
    private JLabel totalPointsLabel;
    private JLabel nextLevelLabel;

    public UserProfile(Map<String, String> userDatta) {
        this.userData = userDatta;

        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        frame = new JFrame("FLYIGU - Profil");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        // Ana panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Profil başlığı
        JLabel titleLabel = new JLabel("Profil Bilgileri");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Kişisel bilgiler paneli
        JPanel personalInfoPanel = createPersonalInfoPanel();
        mainPanel.add(personalInfoPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Üyelik bilgileri paneli
        JPanel membershipPanel = createMembershipPanel();
        mainPanel.add(membershipPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Puan geçmişi paneli
        JPanel pointsHistoryPanel = createPointsHistoryPanel();
        mainPanel.add(pointsHistoryPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Butonlar paneli
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton guncelle = new JButton("Şifre Değiştir");
        guncelle.addActionListener(e -> new updateInfo());

        JButton editProfileButton = new JButton("Profili Düzenle");
        editProfileButton.addActionListener(e -> showEditProfileDialog());
        
        JButton backButton = new JButton("Geri Dön");
        backButton.addActionListener(e -> frame.dispose());
        
        buttonPanel.add(editProfileButton);
        buttonPanel.add(backButton);
        buttonPanel.add(guncelle);
        mainPanel.add(buttonPanel);

        // Frame'i ayarla
        frame.setContentPane(new JScrollPane(mainPanel));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Kullanıcı verilerini yükle
        loadUserData();
    }

    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Kişisel Bilgiler"));

        panel.add(new JLabel("Ad Soyad:"));
        panel.add(new JLabel(userData.get("name") + " " + userData.get("surname")));
        
        panel.add(new JLabel("E-posta:"));
        panel.add(new JLabel(userData.get("mail")));

        return panel;
    }

    private JPanel createMembershipPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Sık Uçuş Programı"));

        membershipLevelLabel = new JLabel("Üyelik Seviyesi: Yükleniyor...");
        membershipLevelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(membershipLevelLabel);
        panel.add(Box.createVerticalStrut(10));

        totalPointsLabel = new JLabel("Toplam Puan: Yükleniyor...");
        totalPointsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(totalPointsLabel);
        panel.add(Box.createVerticalStrut(10));

        pointsProgressBar = new JProgressBar(0, 100);
        pointsProgressBar.setStringPainted(true);
        panel.add(pointsProgressBar);
        panel.add(Box.createVerticalStrut(5));

        nextLevelLabel = new JLabel("Bir sonraki seviyeye kalan: Yükleniyor...");
        nextLevelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(nextLevelLabel);

        return panel;
    }

    private JPanel createPointsHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Puan Geçmişi"));

        String[] columnNames = {"Tarih", "İşlem", "Kazanılan Puan", "Toplam Puan"};
        Object[][] data = new Object[0][4]; // Veriler loadUserData() içinde doldurulacak
        
        JTable table = new JTable(data, columnNames);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private void loadUserData() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javapro", "root", "")) {
            // Kullanıcının puan bilgilerini yükle
            String pointsQuery = "SELECT * FROM user_points WHERE user_id = ?";
            PreparedStatement pointsStmt = conn.prepareStatement(pointsQuery);
            pointsStmt.setString(1, userData.get("id"));
            ResultSet pointsRs = pointsStmt.executeQuery();

            if (pointsRs.next()) {
                int totalPoints = pointsRs.getInt("total_points");
                int currentLevelId = pointsRs.getInt("current_level_id");

                // Üyelik seviyesi bilgilerini al
                String levelQuery = "SELECT * FROM membership_levels WHERE id = ?";
                PreparedStatement levelStmt = conn.prepareStatement(levelQuery);
                levelStmt.setInt(1, currentLevelId);
                ResultSet levelRs = levelStmt.executeQuery();

                if (levelRs.next()) {
                    String levelName = levelRs.getString("level_name");
                    int minPoints = levelRs.getInt("min_points");
                    int maxPoints = levelRs.getInt("max_points");

                    membershipLevelLabel.setText("Üyelik Seviyesi: " + levelName);
                    totalPointsLabel.setText("Toplam Puan: " + totalPoints);

                    // Progress bar'ı güncelle
                    int progress = ((totalPoints - minPoints) * 100) / (maxPoints - minPoints);
                    pointsProgressBar.setValue(progress);
                    
                    int pointsToNextLevel = maxPoints - totalPoints;
                    nextLevelLabel.setText("Bir sonraki seviyeye kalan: " + pointsToNextLevel + " puan");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Kullanıcı bilgileri yüklenirken hata oluştu: " + e.getMessage());
        }
    }

    private void showEditProfileDialog() {
        JDialog dialog = new JDialog(frame, "Profili Düzenle", true);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.setSize(400, 300);

        JTextField nameField = new JTextField(userData.get("name"));
        JTextField surnameField = new JTextField(userData.get("surname"));
        JTextField emailField = new JTextField(userData.get("mail"));

        dialog.add(new JLabel("Ad:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Soyad:"));
        dialog.add(surnameField);
        dialog.add(new JLabel("E-posta:"));
        dialog.add(emailField);

        JButton saveButton = new JButton("Kaydet");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javapro", "root", "")) {
                    String query = "UPDATE users SET name = ?, surname = ?, mail = ? WHERE id = ?";
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    
                    pstmt.setString(1, nameField.getText());
                    pstmt.setString(2, surnameField.getText());
                    pstmt.setString(3, emailField.getText());
                    pstmt.setString(4, userData.get("id"));

                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        // Kullanıcı verilerini güncelle
                        userData.put("name", nameField.getText());
                        userData.put("surname", surnameField.getText());
                        userData.put("mail", emailField.getText());

                        // Arayüzü güncelle
                        loadUserData();
                        dialog.dispose();
                        JOptionPane.showMessageDialog(frame, "Profil başarıyla güncellendi.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, "Profil güncellenirken hata oluştu: " + ex.getMessage());
                }
            }
        });

        dialog.add(saveButton);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }
} 