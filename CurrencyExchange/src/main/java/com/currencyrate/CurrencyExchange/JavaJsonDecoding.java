package com.currencyrate.CurrencyExchange;

import org.json.JSONObject;

public class JavaJsonDecoding {
    public static void main(String[] args) {
        String jsonString ="{\"Full Name\":\"Ritu Sharma\",\"Tuition Fees\":65400.0,\"Roll No.\":1704310046}";

//        Object obj = JSONValue.parse(jsonString);
//        JSONObject jsonObject = (JSONObject) obj;
//        String name = (String) jsonObject.get("Full Name");
//        double fees = (Double) jsonObject.get("Tuition Fees");
//        long rollNo = (Long) jsonObject.get("Roll No.");

        
        
        JSONObject json =new JSONObject(jsonString);
        String name= json.optString("Full Name");
        double fees= json.optDouble("Tuition Fees");
        long rollNo= json.optLong("Roll No.");
        
        
        System.out.println(name + "\n " + fees + "\n " + rollNo);
    }
}