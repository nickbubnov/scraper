package bubnov.scraper.listeners;


import java.io.IOException;
import java.io.InputStream;

public class InputStreamTokenizer extends TokenSender<Character> {
    public void tokenizeStream(InputStream inputStream) throws IOException {
        //TODO use buffered
        int current = inputStream.read();
        while (current != -1) {
            sendToken((char) current);
            current = inputStream.read();
        }
        flush();
        inputStream.close();
    }
}
