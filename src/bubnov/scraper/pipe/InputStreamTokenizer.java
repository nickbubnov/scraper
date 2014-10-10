package bubnov.scraper.pipe;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamTokenizer extends TokenSender<Character> {
    public void tokenizeStream(InputStream inputStream) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        int current = bufferedInputStream.read();
        while (current != -1) {
            sendToken((char) current);
            current = bufferedInputStream.read();
        }
        flush();
        bufferedInputStream.close();
    }
}
