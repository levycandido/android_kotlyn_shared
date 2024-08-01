package com.letech.shared;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends AppCompatActivity {

    private static final String TAG = "SearchTokenActivity";
    private static final String SERVER_KEY = "YOUR_SERVER_KEY"; // Substitua pela sua chave do servidor Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_token);

        // Suponha que você tenha o token e a mensagem
        String token = "DESTINATION_DEVICE_TOKEN";
        String message = "Sua mensagem personalizada aqui";

        sendMessageToToken(token, message);
    }

    private void sendMessageToToken(String token, String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("to", token);
                    JSONObject data = new JSONObject();
                    data.put("message", message);
                    payload.put("data", data);

                    URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Authorization", "key=" + SERVER_KEY);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();
                    os.write(payload.toString().getBytes());
                    os.flush();
                    os.close();

                    int responseCode = conn.getResponseCode();
                    Log.d(TAG, "Response Code: " + responseCode);
                } catch (Exception e) {
                    Log.e(TAG, "Error sending FCM message", e);
                }
            }
        }).start();
    }

}
