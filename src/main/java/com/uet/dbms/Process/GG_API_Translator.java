package com.uet.dbms.Process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class GG_API_Translator {

    public static String translate(String langFrom, String langTo, String text) throws IOException {
        // URL getting destination to Duy Script Google Project.
        String urlStr = "https://script.google.com/macros/s/AKfycbwvoAFq_IgZUe86HGj0VHLHI6VwNXmzZ0urHJMbJ7tT-vukgnUO/exec" +
                "?q=" + URLEncoder.encode(text, "UTF-8") +
                "&target=" + langTo +
                "&source=" + langFrom;
        URL url = new URL(urlStr);
        StringBuilder response = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        // creating sample Browser.
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
