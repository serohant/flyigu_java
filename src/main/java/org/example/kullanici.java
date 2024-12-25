package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class kullanici {
    public JFrame frame; // Sınıf seviyesinde frame tanımı
    private JPanel kullanici;
    private JButton biletAlButton;
    private JButton aktifBiletlerimiGörüntüleButton;
    private JButton BilgiGuncelle;
    private JLabel label;
    private JButton oturumuKapatButton;

    public kullanici() {
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
        Main main = new Main();
        Map<String, String> userdatta = main.getUserData();
        label.setText("Hoşgeldin, "+userdatta.get("name")+" "+userdatta.get("surname"));
        // JPanel'i JFrame'e ekle
        frame.setContentPane(this.kullanici);

        initializeButtonActions();

        // Pencereyi görünür yap
        frame.setVisible(true);
    }
    public void initializeButtonActions() {
        biletAlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Son tıklanan buton: Bilet Ara");
                frame.dispose(); // Mevcut pencereyi kapat
                searchticket searchticket = new searchticket(); // Yeni formu aç
            }
        });
        aktifBiletlerimiGörüntüleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Son tıklanan buton: Bilet Ara");
                frame.dispose(); // Mevcut pencereyi kapat
                getActiveTickets getActiveTickets = new getActiveTickets(); // Yeni formu aç
            }
        });
        BilgiGuncelle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Son tıklanan buton: Bilet Ara");
                frame.dispose(); // Mevcut pencereyi kapat
                updateInfo updateInfo = new updateInfo(); // Yeni formu aç
            }
        });
        oturumuKapatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                anasayfa anasayfa = new anasayfa();
            }
        });
    }
}
