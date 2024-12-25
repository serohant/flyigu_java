package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class anasayfa {
    public JFrame frame; // Sınıf seviyesinde frame tanımı
    private JPanel open;
    private JButton girişYapButton;
    private JButton kayitOlButton;
    private JButton hakkimizdaButton;

    // Yapıcı metot
    public anasayfa() {
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
        frame.setContentPane(this.open);


        // Butonları oluştur ve ekle


        // Butonlara tıklama olaylarını ekle
        initializeButtonActions();

        // Pencereyi görünür yap
        frame.setVisible(true);
    }

    // Butonların tıklama olaylarını ayarla
    private void initializeButtonActions() {
        girişYapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Son tıklanan buton: Giriş Yap");
                frame.dispose(); // Mevcut pencereyi kapat
                giris form = new giris(); // Yeni formu aç
            }
        });

        kayitOlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Son tıklanan buton: Kayıt Ol");
                frame.dispose(); // Mevcut pencereyi kapat
                kayit form = new kayit(); // Yeni formu aç
            }
        });

        hakkimizdaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Son tıklanan buton: Hakkımızda");
                frame.dispose();
                hakkimizda form = new hakkimizda(); // Yeni formu aç
            }
        });
    }

}