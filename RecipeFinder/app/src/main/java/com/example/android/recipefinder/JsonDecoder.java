package com.example.android.recipefinder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

/**
 * Created by Andy on 7/27/2015.
 */
public class JsonDecoder {
    private JsonObject decoded = null;

    public JsonDecoder(String json){
        JsonParser parser = new JsonParser();
        decoded = parser.parse(json).getAsJsonObject();
    }

    public String getName(){
        return decoded.get("name").getAsString();
    }

    public String getCuisine(){
        return decoded.get("cuisine").getAsString();
    }

    public String getMethod(){
        if(!decoded.get("cooking_method").isJsonNull()){
            return decoded.get("cooking_method").getAsString();
        }
        else{
            return "";
        }
    }

    public String getServes(){
        return decoded.get("serves").getAsString();
    }

    public String getIngredients(){
        JsonArray ing_array = decoded.get("ingredients").getAsJsonArray();
        String result = "";
        String quantity = "";
        String name = "";
        String unit = "";
        String preparation = "";

        for(JsonElement j : ing_array){
            if(!j.getAsJsonObject().get("quantity").isJsonNull()){
                quantity = j.getAsJsonObject().get("quantity").getAsString() + " ";
            }
            else{
                quantity = "";
            }

            name = j.getAsJsonObject().get("name").getAsString() + " ";
            unit = j.getAsJsonObject().get("unit").getAsString() + " ";

            if(!j.getAsJsonObject().get("preparation").isJsonNull()){
                preparation = j.getAsJsonObject().get("preparation").getAsString() + " ";
            }
            else{
                preparation = "";
            }
            result += quantity + unit + preparation + name + " \n";
        }
        return result;
    }

    public String getTotal() {
        return decoded.get("total").getAsString();
    }

    public ArrayList<String> getSearchResults(){
        JsonArray ing_array = decoded.get("results").getAsJsonArray();
        ArrayList<String> result = new ArrayList<>();
        for(JsonElement j : ing_array){
            result.add(j.getAsJsonObject().get("id").toString().replace("\"", ""));
        }

        return result;
    }

    public String getDirections(){
        JsonArray dir_array = decoded.get("directions").getAsJsonArray();
        String result = "";
        int step = 1;
        String temp = "";
        for(JsonElement j : dir_array){
            temp = j.toString().replace("\"", "");
            result += step + ") " + temp + "\n";
            step++;
        }
        return result;
    }

    public String getImageURL(){
        return decoded.get("image").getAsString();
    }
}