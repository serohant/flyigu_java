package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class flightApi {

    public static JsonArray oneway(String departure_airport_code, String arrival_airport_code, String departure_date, int number_of_adults, int number_of_children, int number_of_infants, String cabin_class) {
        String apiKey = "676c65a4e5d91744d910251e";

        String url = "https://api.flightapi.io/onewaytrip/" + apiKey + "/" + departure_airport_code + "/" + arrival_airport_code + "/" + departure_date + "/" + number_of_adults + "/" + number_of_children + "/" + number_of_infants + "/" + cabin_class + "/TRY";

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String jsonResponse = response.body();

                JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
                return root.getAsJsonArray("itineraries");
            } else {
                System.err.println("Hata oluştu, yanıt kodu: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }

        // Hata durumunda null döndür
        return null;
    }

    public static JsonObject twoway(String departure_airport_code, String arrival_airport_code, String departure_date, String arrival_date, int number_of_adults, int number_of_children, int number_of_infants, String cabin_class) {
        String apiKey = "676c65a4e5d91744d910251e";
        String url = "https://api.flightapi.io/roundtrip/" + apiKey + "/" + departure_airport_code + "/" + arrival_airport_code + "/" + departure_date + "/" + arrival_date + "/" + number_of_adults + "/" + number_of_children + "/" + number_of_infants + "/" + cabin_class + "/TRY";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String jsonResponse = response.body();
                // Yanıt bir JsonObject ise, doğrudan döndür
                return JsonParser.parseString(jsonResponse).getAsJsonObject();
            } else {
                System.err.println("API hatası: " + response.statusCode() + ", Yanıt: " + response.body());
            }
        } catch (Exception e) {
            System.err.println("Hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

}
