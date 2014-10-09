package bubnov.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


public class WebReader {
    public static InputStream readPage(String url) throws IOException {
        URL myURL = new URL(url);
        URLConnection myURLConnection = myURL.openConnection();
        myURLConnection.connect();
        return myURLConnection.getInputStream();
    }
}
