package biz.kytech.pushnotification.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class NotificationMessage {

    private String title;
    private String body;
    private Category category;
    private Bitmap image;

    public enum Category {
        Default("default"),
        Image("image");

        private String value;

        Category(String value) {
            this.value = value;
        }

        public static Category parse(String value) {
            if (value == null) { return Default; }
            if (value.equals(Image.value)) { return Image; }
            return Default;
        }
    }

    public NotificationMessage(Map<String, String> data) {
        title = data.get("title");
        body = data.get("body");
        category = Category.parse(data.get("category"));
        image = null;

        if (category == Category.Image) {
            try {
                String json = data.get("rich");
                JSONObject rich = new JSONObject(json);
                URL url = new URL(rich.getString("url"));
                image = downloadBitmap(url);
            } catch (Exception e) {
                e.printStackTrace();
                category = Category.Default;
            }
        }
    }

    private static Bitmap downloadBitmap(URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        return bitmap;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Category getCategory() {
        return category;
    }

    public Bitmap getImage() {
        return image;
    }
}
