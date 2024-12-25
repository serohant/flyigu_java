package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class yonetici {
    public JFrame frame;
    private JPanel yonetici;
    private JButton satılanBiletleriListeleButton;
    private JButton kullanıcılarıListeleButton;
    private JButton duyuruYapButton;
    private JButton kggec;

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
    };
}
