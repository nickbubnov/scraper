package bubnov.crawler.listeners;


import java.util.ArrayList;
import java.util.Collection;

public class SentenceTokenizer extends TokenSender<Collection<String>> implements TokenListener<String>{
    private Collection<String> currentSentence;
    private final TokenSender<String> mySource;

    public SentenceTokenizer(TokenSender<String> source) {
        currentSentence = new ArrayList<String>();
        mySource = source;
        mySource.registerListener(this);
    }

    @Override
    public void receive(String word) {
        System.out.println("received word " + word);
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

    @Override
    public void unregister() {
        mySource.removeListener(this);
    }
}
