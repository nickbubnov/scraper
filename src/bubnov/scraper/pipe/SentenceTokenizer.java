package bubnov.scraper.pipe;


import java.util.ArrayList;
import java.util.Collection;

public class SentenceTokenizer extends TokenSender<Collection<String>> implements TokenListener<String>{
    private Collection<String> currentSentence;

    public SentenceTokenizer(TokenSender<String> source) {
        currentSentence = new ArrayList<String>();
        source.registerListener(this);
    }

    @Override
    public void receive(String word) {
        if (!word.equals(".")) {
            currentSentence.add(word);
        } else {
            flush();
        }
    }

    @Override
    public void flush() {
        if (!currentSentence.isEmpty()) {
            sendToken(currentSentence);
            currentSentence = new ArrayList<String>();
        } else {
            super.flush();
        }
    }
}
