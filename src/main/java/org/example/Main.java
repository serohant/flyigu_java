package org.example;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.google.gson.JsonArray;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;

public class Main {
    public String[] userData;
    public JFrame frame; // Sınıf seviyesinde frame tanımı
    private int isLoggedIn = 0; // Varsayı
    private static Map<String, String> userdata;

    public static void main(String[] args) throws IOException, InterruptedException {
        Main app = new Main(); // Main sınıfından bir nesne oluştur

        app.start(); // Uygulamayı başlat

        flightApi api = new flightApi();

    }

    public static void addUserData(Map<String, String> usserdata) {
        userdata = usserdata;
    }

    public static Map<String, String> getUserData() {
        return userdata;
    }

    // Giriş durumuna göre uygun ekranı başlat
    public void start() {
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

        // Pencereyi görünür yap
        frame.setVisible(false);
        if (isLoggedIn == 0) {
            anasayfa form = new anasayfa();
        }

        System.out.println("Uygulama çalışıyor...");
    }

    // Giriş durumunu döndüren metot
    public int getLoginStatus() {
        return isLoggedIn; // Sınıf seviyesindeki değişkeni döndür
    }

    public void show(String type){
        switch (type){
            case "0":
                kullanici kullanici = new kullanici();
                break;
            case "1":
                yonetici yonetici = new yonetici();
        }
    }
}
