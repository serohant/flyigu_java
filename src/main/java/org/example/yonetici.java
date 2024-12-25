package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class yonetici {
    public JFrame frame;
    private JPanel yonetici;
    private JButton satılanBiletleriListeleButton;
    private JButton kullanıcılarıListeleButton;
    private JButton duyuruYapButton;
    private JButton kggec;
    private JButton promosyonKodlarıButton;

    Main main = new Main();
    public yonetici() {
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
        frame.setContentPane(this.yonetici);

        // Butonlara tıklama olaylarını ekle
        initializeButtonActions();
        // Pencereyi görünür yap
        frame.setVisible(true);
    }
    private void initializeButtonActions() {
        duyuruYapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Son tıklanan buton: Kullanıcı Listele");
                frame.dispose();
                duyuru duyuru = new duyuru();
            }
        });
        kullanıcılarıListeleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Son tıklanan buton: Kullanıcı Listele");
                frame.dispose();
                ykullanicilistesi ykullanicilistesi = new ykullanicilistesi();
            }
        });
        satılanBiletleriListeleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Son tıklanan buton: Kullanıcı Listele");
                frame.dispose();
                ytickets ytickets = new ytickets();
            }
        });
        kggec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Son tıklanan buton: Kullanıcı Listele");
                frame.dispose();
                kullanici kullanici = new kullanici();
            }
        });
        promosyonKodlarıButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPromotionCodesPanel();
            }
        });
    };
    private void showPromotionCodesPanel() {
        JFrame promoFrame = new JFrame("Promosyon Kodları Yönetimi");
        promoFrame.setSize(1000, 600);
        promoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Tablo modeli
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Kod");
        model.addColumn("İndirim (%)");
        model.addColumn("Aktif");
        model.addColumn("Son Kullanma");
        model.addColumn("Min. Tutar");
        model.addColumn("Kullanım Limiti");
        model.addColumn("Kullanım Sayısı");

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Butonlar paneli
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Yeni Kod Ekle");
        JButton editButton = new JButton("Düzenle");
        JButton deleteButton = new JButton("Sil");
        JButton refreshButton = new JButton("Yenile");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Verileri yükleme fonksiyonu
        ActionListener refreshAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setRowCount(0);
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javapro", "root", "")) {
                    String query = "SELECT * FROM promotion_codes ORDER BY id DESC";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()) {
                        model.addRow(new Object[]{
                                rs.getInt("id"),
                                rs.getString("code"),
                                rs.getInt("discount_percentage"),
                                rs.getBoolean("is_active") ? "Evet" : "Hayır",
                                rs.getDate("expiry_date"),
                                rs.getDouble("min_purchase_amount"),
                                rs.getInt("usage_limit"),
                                rs.getInt("usage_count")
                        });
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(promoFrame, "Veriler yüklenirken hata oluştu: " + ex.getMessage());
                }
            }
        };

        // Yeni kod ekleme
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = new JDialog(promoFrame, "Yeni Promosyon Kodu", true);
                dialog.setLayout(new GridLayout(7, 2, 5, 5));

                JTextField codeField = new JTextField();
                JTextField discountField = new JTextField();
                JCheckBox activeBox = new JCheckBox("", true);
                JTextField expiryField = new JTextField("YYYY-MM-DD");
                JTextField minAmountField = new JTextField();
                JTextField limitField = new JTextField();

                dialog.add(new JLabel("Kod:"));
                dialog.add(codeField);
                dialog.add(new JLabel("İndirim (%):"));
                dialog.add(discountField);
                dialog.add(new JLabel("Aktif:"));
                dialog.add(activeBox);
                dialog.add(new JLabel("Son Kullanma:"));
                dialog.add(expiryField);
                dialog.add(new JLabel("Min. Tutar:"));
                dialog.add(minAmountField);
                dialog.add(new JLabel("Kullanım Limiti:"));
                dialog.add(limitField);

                JButton saveButton = new JButton("Kaydet");
                saveButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javapro", "root", "")) {
                            String query = "INSERT INTO promotion_codes (code, discount_percentage, is_active, expiry_date, min_purchase_amount, usage_limit) VALUES (?, ?, ?, ?, ?, ?)";
                            PreparedStatement pstmt = conn.prepareStatement(query);
                            pstmt.setString(1, codeField.getText());
                            pstmt.setInt(2, Integer.parseInt(discountField.getText()));
                            pstmt.setBoolean(3, activeBox.isSelected());
                            pstmt.setString(4, expiryField.getText().equals("YYYY-MM-DD") ? null : expiryField.getText());
                            pstmt.setDouble(5, Double.parseDouble(minAmountField.getText()));
                            pstmt.setInt(6, Integer.parseInt(limitField.getText()));
                            pstmt.executeUpdate();

                            dialog.dispose();
                            refreshAction.actionPerformed(null);
                            JOptionPane.showMessageDialog(promoFrame, "Promosyon kodu başarıyla eklendi.");
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(dialog, "Kod eklenirken hata oluştu: " + ex.getMessage());
                        }
                    }
                });
                dialog.add(saveButton);

                dialog.pack();
                dialog.setLocationRelativeTo(promoFrame);
                dialog.setVisible(true);
            }
        });

        // Kod düzenleme
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(promoFrame, "Lütfen düzenlenecek bir kod seçin.");
                    return;
                }

                JDialog dialog = new JDialog(promoFrame, "Promosyon Kodu Düzenle", true);
                dialog.setLayout(new GridLayout(7, 2, 5, 5));

                JTextField codeField = new JTextField(model.getValueAt(selectedRow, 1).toString());
                JTextField discountField = new JTextField(model.getValueAt(selectedRow, 2).toString());
                JCheckBox activeBox = new JCheckBox("", model.getValueAt(selectedRow, 3).equals("Evet"));
                JTextField expiryField = new JTextField(model.getValueAt(selectedRow, 4) != null ? model.getValueAt(selectedRow, 4).toString() : "YYYY-MM-DD");
                JTextField minAmountField = new JTextField(model.getValueAt(selectedRow, 5).toString());
                JTextField limitField = new JTextField(model.getValueAt(selectedRow, 6).toString());

                dialog.add(new JLabel("Kod:"));
                dialog.add(codeField);
                dialog.add(new JLabel("İndirim (%):"));
                dialog.add(discountField);
                dialog.add(new JLabel("Aktif:"));
                dialog.add(activeBox);
                dialog.add(new JLabel("Son Kullanma:"));
                dialog.add(expiryField);
                dialog.add(new JLabel("Min. Tutar:"));
                dialog.add(minAmountField);
                dialog.add(new JLabel("Kullanım Limiti:"));
                dialog.add(limitField);

                JButton updateButton = new JButton("Güncelle");
                updateButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javapro", "root", "")) {
                            String query = "UPDATE promotion_codes SET code = ?, discount_percentage = ?, is_active = ?, expiry_date = ?, min_purchase_amount = ?, usage_limit = ? WHERE id = ?";
                            PreparedStatement pstmt = conn.prepareStatement(query);
                            pstmt.setString(1, codeField.getText());
                            pstmt.setInt(2, Integer.parseInt(discountField.getText()));
                            pstmt.setBoolean(3, activeBox.isSelected());
                            pstmt.setString(4, expiryField.getText().equals("YYYY-MM-DD") ? null : expiryField.getText());
                            pstmt.setDouble(5, Double.parseDouble(minAmountField.getText()));
                            pstmt.setInt(6, Integer.parseInt(limitField.getText()));
                            pstmt.setInt(7, Integer.parseInt(model.getValueAt(selectedRow, 0).toString()));
                            pstmt.executeUpdate();

                            dialog.dispose();
                            refreshAction.actionPerformed(null);
                            JOptionPane.showMessageDialog(promoFrame, "Promosyon kodu başarıyla güncellendi.");
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(dialog, "Kod güncellenirken hata oluştu: " + ex.getMessage());
                        }
                    }
                });
                dialog.add(updateButton);

                dialog.pack();
                dialog.setLocationRelativeTo(promoFrame);
                dialog.setVisible(true);
            }
        });

        // Kod silme
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(promoFrame, "Lütfen silinecek bir kod seçin.");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(promoFrame,
                        "Bu promosyon kodunu silmek istediğinizden emin misiniz?",
                        "Silme Onayı",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javapro", "root", "")) {
                        String query = "DELETE FROM promotion_codes WHERE id = ?";
                        PreparedStatement pstmt = conn.prepareStatement(query);
                        pstmt.setInt(1, Integer.parseInt(model.getValueAt(selectedRow, 0).toString()));
                        pstmt.executeUpdate();

                        refreshAction.actionPerformed(null);
                        JOptionPane.showMessageDialog(promoFrame, "Promosyon kodu başarıyla silindi.");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(promoFrame, "Kod silinirken hata oluştu: " + ex.getMessage());
                    }
                }
            }
        });

        // Yenile butonu
        refreshButton.addActionListener(refreshAction);

        // İlk yükleme
        refreshAction.actionPerformed(null);

        promoFrame.setContentPane(mainPanel);
        promoFrame.setLocationRelativeTo(null);
        promoFrame.setVisible(true);
    }
}
