package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class hakkimizda {
    private static JFrame frame;
    private JPanel hakkimizda; // IntelliJ GUI Designer'dan oluşturulan panel
    private JButton geriDönButton;

    public hakkimizda() {
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
        frame.setContentPane(this.hakkimizda);

        // Butonlara tıklama olaylarını ekle
        // Pencereyi görünür yap
        frame.setVisible(true);
        geriDönButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Son tıklanan buton: Geri dön");
                frame.dispose(); // Mevcut pencereyi kapat
                anasayfa anasayfa = new anasayfa(); // Yeni formu aç
            }
        });
    }
}
