package com.currencyrate.CurrencyExchange;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;

public class CurrencyExchange {

    private static final String API_KEY = "8104ae5268e69a67f7816d0a";
//    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";

    public static double getRate(String from, String to) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String url = BASE_URL + API_KEY + "/latest/" + to;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = client.send(request,
            HttpResponse.BodyHandlers.ofString());

        JSONObject json = new JSONObject(response.body());

//        if (!json.getString("result").equals("success")) {
//            throw new RuntimeException("API error: " + json.optString("error-type"));
//        }
//
//        return json.getDouble("conversion_rate");
      //  System.out.println(json.toString(4));
        JSONObject currencyRateJSON=json.getJSONObject("conversion_rates");
        System.out.println("USD : "+currencyRateJSON.getDouble("USD"));
        return currencyRateJSON.getDouble("EUR");
        
    }

    public static void main(String[] args) throws Exception {
       double rate= getRate("PLN", "EUR");
        System.out.printf("1 PLN = %.4f EUR%n", rate);

        // Convert an amount
//        double amount = 500;
//        System.out.printf("%.2f PLN = %.2f EUR%n", amount, amount * rate);
    }
}