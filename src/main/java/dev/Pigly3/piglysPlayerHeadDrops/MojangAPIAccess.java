package dev.Pigly3.piglysPlayerHeadDrops;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class MojangAPIAccess {
    public static String uuidToUsername(UUID uuid) throws IOException {
        String uuidString = uuid.toString().replace("-", "");
        URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            return null;
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
            return json.get("name").getAsString();
        }
    }

    public static String getSkin(UUID uuid) throws IOException {
        return getSkin(uuid.toString().replace("-", ""));
    }

    public static String getSkin(String uuidString) throws IOException {
        URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidString + "?unsigned=false");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            return null;
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonArray arr =  json.get("properties").getAsJsonArray();
            return arr.get(0).getAsJsonObject().get("value").getAsString();
        }
    }

    public static UUID getUUID(String username) throws IOException {
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/"+username);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            return null;
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
            String str = json.get("id").getAsString();
            return APIManager.parseUUID(str);
        }
    }
}
