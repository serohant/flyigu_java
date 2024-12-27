package org.example;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class listFlights {
    public JFrame frame;
    private JTable table1;
    private JPanel listFlights;
    private JButton geriDönButton;
    private JButton seçilenIleDevamEtButton;
    private Map<String, Object> flightdata;

    public listFlights(Map<String, Object> data) {
        flightdata = data;
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }

        frame = new JFrame("FLYIGU");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 400);
        listFlights.setLayout(null);

        table1 = new JTable();
        JScrollPane scrollPane = new JScrollPane(table1);
        scrollPane.setBounds(10, 10, 860, 250);
        listFlights.add(scrollPane);

        geriDönButton.setBounds(10, 270, 150, 30);
        seçilenIleDevamEtButton.setBounds(170, 270, 150, 30);
        listFlights.add(geriDönButton);
        listFlights.add(seçilenIleDevamEtButton);

        frame.setContentPane(listFlights);
        Main main = new Main();
        flightApi aapi = new flightApi();
        if (data.get("is_round").equals(true)) {
            try {
                System.out.println("Gidiş-Dönüş Uçuş:");
                JsonObject twowayresults = aapi.twoway(data.get("departure_airport").toString(), data.get("arrival_airport").toString(), data.get("departure_date").toString(), data.get("return_date").toString(), Integer.parseInt(data.get("adult_count").toString()), Integer.parseInt(data.get("child_count").toString()), Integer.parseInt(data.get("baby_count").toString()), data.get("cabin_type").toString());
                if (twowayresults != null) {
                    setupTableFromJson(twowayresults, data);
                } else {
                    System.out.println("Hata oluştu veya sonuç yok.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                System.out.println("Tek Yön Uçuş:");
                JsonArray onewayResults = aapi.oneway(data.get("departure_airport").toString(), data.get("arrival_airport").toString(), data.get("departure_date").toString(), Integer.parseInt(data.get("adult_count").toString()), Integer.parseInt(data.get("child_count").toString()), Integer.parseInt(data.get("baby_count").toString()), data.get("cabin_type").toString());
                if (onewayResults != null) {
                    setupTable(onewayResults, data);
                } else {
                    System.out.println("Hata oluştu veya sonuç yok.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        seçilenIleDevamEtButton.addActionListener(e -> {
            // Seçili satırın bilgilerini al
            int selectedRow = table1.getSelectedRow();
            if (selectedRow != -1) {
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                String departure = model.getValueAt(selectedRow, 1).toString();
                String arrival = model.getValueAt(selectedRow, 2).toString();
                String price = model.getValueAt(selectedRow, 6).toString();

                // Seçili satırın bilgilerini göster
                String[] options = { "Vazgeç", "Devam Et" };
                var selection = JOptionPane.showOptionDialog(frame, "Seçilen Uçuş Bilgileri:\n" +
                                "Gidiş Kalkış: " + departure + "\n" +
                                "Gidiş Varış: " + arrival + "\n" +
                                "Fiyat: " + price + " TRY", "Onaylıyor musunuz?",
                        0, 3, null, options, options[1]);
                if (selection == 0) {

                }
                if (selection == 1) {
                    paymentPage paymentPage = new paymentPage(price, flightdata, main.getUserData());
                    frame.dispose();
                    JOptionPane.showMessageDialog(null, "Ödeme adımına yönlendiriliyorsunuz");
                }
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Lütfen bir uçuş seçin!",
                        "Hata",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        geriDönButton.addActionListener(e -> {
            // Seçili satırın bilgilerini al
            frame.dispose();
        });
        frame.setVisible(true);
    }

    private void setupTable(JsonArray flights, Map<String, Object> data) {
        String[] columnNames = {"Uçuş ID", "Kalkış Havaalanı", "Varış Havaalanı", "Kalkış Saati", "Varış Saati", "Aktarma", "Fiyat (TRY)"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try {
            List<JsonObject> sortedFlights = new ArrayList<>();
            for (JsonElement flightElement : flights) {
                sortedFlights.add(flightElement.getAsJsonObject());
            }

            sortedFlights.sort((f1, f2) -> {
                double price1 = f1.getAsJsonArray("pricing_options").get(0).getAsJsonObject().getAsJsonObject("price").get("amount").getAsDouble();
                double price2 = f2.getAsJsonArray("pricing_options").get(0).getAsJsonObject().getAsJsonObject("price").get("amount").getAsDouble();
                return Double.compare(price1, price2);
            });

            int maxFlights = Math.min(sortedFlights.size(), 10);
            if(sortedFlights.size() != 0){
                for (int i = 0; i < maxFlights; i++) {
                    JsonObject flight = sortedFlights.get(i);

                    String flightId = flight.get("id").getAsString();
                    JsonObject priceObject = flight.getAsJsonArray("pricing_options").get(0).getAsJsonObject();
                    double price = priceObject.getAsJsonObject("price").get("amount").getAsDouble();

                    String departureAirport = data.get("departure_airport").toString();
                    String arrivalAirport = data.get("arrival_airport").toString();
                    String departureTime = parseCustomTimestamp(flightId.split("-")[1]);
                    String arrivalTime = parseCustomTimestamp(flightId.split("-")[6]);
                    String aktarma = Integer.parseInt(flightId.split("-")[3]) > 0 ? "Aktarmalı" : "Aktarmasız";

                    model.addRow(new Object[]{flightId, departureAirport, arrivalAirport, departureTime, arrivalTime, aktarma, price});
                }
            }else{
                JOptionPane.showMessageDialog(frame, "İstenilen tipte bir uçuş bulunamadı");
            }
        } catch (Exception ex) {
            System.err.println("JSON verisi işlenirken hata oluştu: " + ex.getMessage());
        }

        table1.setModel(model);
    }
    private static String parseCustomTimestamp(String timestamp) {
        try {
            int year = Integer.parseInt(timestamp.substring(0, 2)) + 2000;
            int month = Integer.parseInt(timestamp.substring(2, 4));
            int day = Integer.parseInt(timestamp.substring(4, 6));
            int hour = Integer.parseInt(timestamp.substring(6, 8));
            int min = Integer.parseInt(timestamp.substring(8, 10));

            LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, min);
            return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        } catch (Exception e) {
            return "Geçersiz Zaman";
        }
    }
    private void setupTableFromJson(JsonObject jsonObject, Map<String, Object> data) {
        JsonArray itineraries = jsonObject.getAsJsonArray("itineraries");
        JsonArray legs = jsonObject.getAsJsonArray("legs");

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Gidiş Kalkış");
        model.addColumn("Gidiş Varış");
        model.addColumn("Dönüş Kalkış");
        model.addColumn("Dönüş Varış");
        model.addColumn("Gidiş Süresi");
        model.addColumn("Dönüş Süresi");
        model.addColumn("Fiyat (TRY)");
        table1.setModel(model);

        try {
            List<JsonObject> sortedItineraries = new ArrayList<>();
            for (JsonElement itineraryElement : itineraries) {
                sortedItineraries.add(itineraryElement.getAsJsonObject());
            }

            sortedItineraries.sort((itinerary1, itinerary2) -> {
                double price1 = itinerary1.getAsJsonArray("pricing_options").get(0).getAsJsonObject().getAsJsonObject("price").get("amount").getAsDouble();
                double price2 = itinerary2.getAsJsonArray("pricing_options").get(0).getAsJsonObject().getAsJsonObject("price").get("amount").getAsDouble();
                return Double.compare(price1, price2);
            });

            int maxResults = Math.min(sortedItineraries.size(), 10);
            if(sortedItineraries.size() != 0){
                for (int i = 0; i < maxResults; i++) {
                    JsonObject itinerary = sortedItineraries.get(i);

                    JsonArray legIds = itinerary.getAsJsonArray("leg_ids");
                    if (legIds.size() < 2) continue;

                    JsonObject outboundLeg = findLegById(legs, legIds.get(0).getAsString());
                    JsonObject inboundLeg = findLegById(legs, legIds.get(1).getAsString());

                    if (outboundLeg == null || inboundLeg == null) continue;

                    String departureTimeOutbound = outboundLeg.get("departure").getAsString();
                    String arrivalTimeOutbound = outboundLeg.get("arrival").getAsString();
                    int durationOutbound = outboundLeg.get("duration").getAsInt();

                    String departureTimeInbound = inboundLeg.get("departure").getAsString();
                    String arrivalTimeInbound = inboundLeg.get("arrival").getAsString();
                    int durationInbound = inboundLeg.get("duration").getAsInt();

                    JsonArray pricingOptions = itinerary.getAsJsonArray("pricing_options");
                    double price = pricingOptions.size() > 0 ? pricingOptions.get(0).getAsJsonObject().getAsJsonObject("price").get("amount").getAsDouble() : 0.0;

                    model.addRow(new Object[]{
                            formatDateTime(departureTimeOutbound),
                            formatDateTime(arrivalTimeOutbound),
                            formatDateTime(departureTimeInbound),
                            formatDateTime(arrivalTimeInbound),
                            formatDuration(durationOutbound),
                            formatDuration(durationInbound),
                            price
                    });
                }
            }else{
                JOptionPane.showMessageDialog(frame, "İstenilen tipte bir uçuş bulunamadı");
            }
        } catch (Exception ex) {
            System.err.println("JSON işlenirken hata: " + ex.getMessage());
        }
    }

    private JsonObject findLegById(JsonArray legs, String legId) {
        for (JsonElement legElement : legs) {
            JsonObject leg = legElement.getAsJsonObject();
            if (leg.get("id").getAsString().equals(legId)) {
                return leg;
            }
        }
        return null;
    }

    private String formatDateTime(String dateTime) {
        return dateTime.replace("T", " ").substring(0, 16);
    }

    private String formatDuration(int duration) {
        int hours = duration / 60;
        int minutes = duration % 60;
        return hours + "s " + minutes + "dk";
    }
}
