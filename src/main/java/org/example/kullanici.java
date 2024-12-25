package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class kullanici {
    private JFrame frame;
    private JPanel kullanici;
    private JButton biletAlButton;
    private JButton biletlerimButton;
    private JButton çıkışYapButton;
    private JLabel label1;
    private JButton profilButton;
    private Map<String, String> userData;

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
        label1.setText("Hoşgeldin, "+userdatta.get("name")+" "+userdatta.get("surname"));
        // JPanel'i JFrame'e ekle
        frame.setContentPane(this.kullanici);

        // Butonlara tıklama olaylarını ekle
        // Pencereyi görünür yap
        frame.setVisible(true);

        biletAlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                searchticket searchticket = new searchticket();
            }
        });

        biletlerimButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                getActiveTickets getActiveTickets = new getActiveTickets();
            }
        });

        profilButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserProfile(userdatta);
            }
        });

        çıkışYapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new giris();
            }
        });
    }

    public void setUserData(Map<String, String> userData) {
        this.userData = userData;
    }
}
