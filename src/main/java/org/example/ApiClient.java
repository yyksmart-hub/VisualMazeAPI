package org.example;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.HttpURLConnection;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.InputStream;

public class ApiClient {

    // הגדרת כתובת הבסיס כקבוע - הדרך הנכונה והמסודרת לעבוד מול API!
    private static final String BASE_URL = "https://backend-qcf9.onrender.com/fm1/";

    private final HttpClient httpClient;
    private final Gson gson;

    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public MazeConfig fetchRenderConfig() {
        // שימוש בקבוע הכתובת
        String url = BASE_URL + "get-render-config";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() ==HttpURLConnection.HTTP_OK ) {
                return gson.fromJson(response.body(), MazeConfig.class);
            } else {
                System.err.println("Error: Server returned status code " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch render config: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public BufferedImage fetchMazeImage(int width, int height) {
        // שימוש בקבוע הכתובת יחד עם הפרמטרים
        String url = BASE_URL + "get-maze-image?width=" + width + "&height=" + height;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() ==HttpURLConnection.HTTP_OK ) {
                return ImageIO.read(response.body());
            } else {
                System.err.println("Error: Server returned status code " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch maze image: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}