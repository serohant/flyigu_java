package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;
import java.util.Arrays;

public class searchticket {
    public JFrame frame;
    private JPanel searchticketPanel;
    private JComboBox<String> comboBox1;
    private JComboBox<String> comboBox2;
    private JSpinner spinner1; // Kalkış tarihi
    private JSpinner spinner2; // Varış tarihi
    private JSpinner spinner3;
    private JSpinner spinner4;
    private JSpinner spinner5;
    private JCheckBox gidişDönüşCheckBox;
    private JRadioButton economyRadioButton;
    private JRadioButton businessRadioButton;
    private JRadioButton firstClassRadioButton;
    private JRadioButton premiumEconomyRadioButton;
    private JButton biletAraButton;
    private JLabel label2;
    private JButton geriDonButton;

    public searchticket() {
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

        // İçeriği JFrame'e ekle
        frame.setContentPane(this.searchticketPanel);

        // ComboBox'lara havaalanlarını ekle
        addAirportsToComboBoxes();

        // Tarih spinner'larını yapılandır
        configureDateSpinners();
        initializeButtonActions();
        // Checkbox için işlem ekle
        configureCheckbox();

        // Pencereyi görünür yap
        frame.setVisible(true);
    }

    private void addAirportsToComboBoxes() {
        // Türkiye'deki havaalanları listesi (isim - kod)
        String[] airports = {
                "Adana Şakirpaşa Havalimanı - ADA",
                "Ankara Esenboğa Havalimanı - ESB",
                "Antalya Havalimanı - AYT",
                "Bodrum Milas Havalimanı - BJV",
                "Dalaman Havalimanı - DLM",
                "Denizli Çardak Havalimanı - DNZ",
                "Diyarbakır Havalimanı - DIY",
                "Elazığ Havalimanı - EZS",
                "Erzurum Havalimanı - ERZ",
                "Gaziantep Havalimanı - GZT",
                "Hatay Havalimanı - HTY",
                "Isparta Süleyman Demirel Havalimanı - ISE",
                "İstanbul Havalimanı - IST",
                "İstanbul Sabiha Gökçen Havalimanı - SAW",
                "İzmir Adnan Menderes Havalimanı - ADB",
                "Kayseri Erkilet Havalimanı - ASR",
                "Konya Havalimanı - KYA",
                "Malatya Erhaç Havalimanı - MLX",
                "Mardin Havalimanı - MQM",
                "Samsun Çarşamba Havalimanı - SZF",
                "Şanlıurfa GAP Havalimanı - GNY",
                "Trabzon Havalimanı - TZX",
                "Van Ferit Melen Havalimanı - VAN"
        };
        Main app = new Main(); // Main sınıfından bir nesne oluştur
        Map<String, String> userData = Main.getUserData();
        System.out.println(userData);
        // Havaalanlarını alfabetik sırayla düzenle
        Arrays.sort(airports);

        // ComboBox'lara havaalanlarını ekle
        for (String airport : airports) {
            comboBox1.addItem(airport);
            comboBox2.addItem(airport);
        }
    }

    private void configureDateSpinners() {
        // Kalkış Tarihi için Spinner
        spinner1.setModel(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor1 = new JSpinner.DateEditor(spinner1, "yyyy-MM-dd");
        spinner1.setEditor(dateEditor1);

        // Varış Tarihi için Spinner (Başlangıçta gizli)
        spinner2.setModel(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor2 = new JSpinner.DateEditor(spinner2, "yyyy-MM-dd");
        spinner2.setEditor(dateEditor2);
        spinner2.setVisible(false); // Başlangıçta gizli
        label2.setVisible(false);
    }

    private void configureCheckbox() {
        gidişDönüşCheckBox.addActionListener(e -> {
            // Checkbox işaretliyse spinner2'yi görünür yap, aksi halde gizle
            spinner2.setVisible(gidişDönüşCheckBox.isSelected());
            label2.setVisible(gidişDönüşCheckBox.isSelected());
            // Değişiklikleri yansıtmak için paneli yeniden çiz
            searchticketPanel.revalidate();
            searchticketPanel.repaint();
        });
    }
    private void initializeButtonActions() {
        geriDonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Son tıklanan buton: Geri dön");
                frame.dispose(); // Mevcut pencereyi kapat
                kullanici kullanici = new kullanici(); // Yeni formu aç
            }
        });
        biletAraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String departureAirport = (String) comboBox1.getSelectedItem();
                    String arrivalAirport = (String) comboBox2.getSelectedItem();

                    // Spinner değerlerini al
                    Date departureDate = (Date) spinner1.getValue();
                    Date returnDate = (Date) spinner2.getValue();

                    // Date'leri LocalDate'e dönüştür
                    LocalDate departureLocalDate = departureDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalDate returnLocalDate = returnDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                    int adult = Integer.parseInt(spinner3.getValue().toString());
                    int children = Integer.parseInt(spinner4.getValue().toString());
                    int baby = Integer.parseInt(spinner5.getValue().toString());

                    // Bugünün tarihi
                    LocalDate today = LocalDate.now();

                    // Checkbox durumunu al
                    boolean isRoundTrip = gidişDönüşCheckBox.isSelected();

                    // Seçili radio button'u kontrol et
                    String selectedClass = "";
                    if (economyRadioButton.isSelected()) {
                        selectedClass = "Economy";
                    } else if (businessRadioButton.isSelected()) {
                        selectedClass = "Business";
                    } else if (firstClassRadioButton.isSelected()) {
                        selectedClass = "First";
                    } else if (premiumEconomyRadioButton.isSelected()) {
                        selectedClass = "Premium_Economy";
                    }

                    // Değerleri bir Map yapısında sakla
                    Map<String, Object> ticketData = new HashMap<>();
                    ticketData.put("departure_airport", departureAirport.split(" - ")[1].trim());
                    ticketData.put("arrival_airport", arrivalAirport.split(" - ")[1].trim());
                    ticketData.put("departure_date", departureLocalDate);
                    ticketData.put("return_date", returnLocalDate);
                    ticketData.put("adult_count", adult);
                    ticketData.put("child_count", children);
                    ticketData.put("baby_count", baby);
                    ticketData.put("is_round", isRoundTrip);
                    ticketData.put("cabin_type", selectedClass);

                    // Kontroller
                    if (ticketData.get("departure_airport").equals(ticketData.get("arrival_airport"))) {
                        JOptionPane.showMessageDialog(null, "Kalkış havalimanı ile varış havalimanı aynı olamaz.");
                    } else if (departureLocalDate.isBefore(today)) {
                        JOptionPane.showMessageDialog(null, "Gidiş tarihi bugünden eski olamaz.");
                    } else if (isRoundTrip && (returnLocalDate.isBefore(departureLocalDate) || returnLocalDate.isEqual(departureLocalDate))) {
                        JOptionPane.showMessageDialog(null, "Dönüş tarihi gidiş tarihinden önce ya da aynı olamaz.");
                    } else if (adult < 1 || adult > 8 || children < 0 || children > 8 || baby < 0 || baby > 8) {
                        JOptionPane.showMessageDialog(null, "Yetişkin sayısı 1-8 arasında olmalıdır. Çocuk ve bebek sayıları 0-8 arasında olmalıdır.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Bilet arama işlemi başarılı!");
                        System.out.println("Son tıklanan buton: Bilet ara");
                        listFlights listFlights = new listFlights(ticketData); // Yeni formu aç
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Hata oluştu: " + ex.getMessage());
                }
            }
        });
    }
}
