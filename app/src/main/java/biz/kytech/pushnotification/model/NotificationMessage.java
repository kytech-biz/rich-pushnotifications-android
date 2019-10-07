package biz.kytech.pushnotification.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class NotificationMessage implements Serializable {

    private static final String TAG = NotificationMessage.class.getSimpleName();

    private String title;
    private String body;
    private Category category;
    private URL url;

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

        if (category == Category.Image) {
            try {
                String json = data.get("rich");
                JSONObject rich = new JSONObject(json);
                url = new URL(rich.getString("url"));
            } catch (Exception e) {
                e.printStackTrace();
                category = Category.Default;
            }
        }
    }

    public Bitmap loadBitmap() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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

    public URL getUrl() {
        return url;
    }
}
