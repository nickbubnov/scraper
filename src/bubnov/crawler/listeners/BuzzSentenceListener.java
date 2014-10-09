package bubnov.crawler.listeners;

import bubnov.crawler.ReportProducer;

import java.io.PrintWriter;
import java.util.*;

public class BuzzSentenceListener implements TokenListener<Collection<String>>, ReportProducer{
    private final TokenSender<Collection<String>> mySource;
    private final List<Collection<String>> mySentences;
    private final Set<String> buzzWords = new HashSet<String>();

    public BuzzSentenceListener(TokenSender<Collection<String>> source,Collection<String> words) {
        mySentences = new ArrayList<Collection<String>>();
        buzzWords.addAll(words);
        mySource = source;
        mySource.registerListener(this);
    }

    @Override
    public void receive(Collection<String> sentence) {
        System.out.println("received sentence : " + sentence);

        boolean hasBuzzWords = false;
        for (String word : sentence) {
            if (buzzWords.contains(word)) {
                hasBuzzWords = true;
                break;
            }
        }

        if (hasBuzzWords) {
            mySentences.add(sentence);
        }
    }

    @Override
    public void flush() {}

    @Override
    public void unregister() {
        mySource.removeListener(this);
    }

    public Collection<Collection<String>> getSentences() {
        return mySentences;
    }

    @Override
    public void report(PrintWriter writer) {
        Collection<Collection<String>> sentences = getSentences();
        writer.println("Found " + sentences.size() + " sentences");

        for (Collection<String> sentence : sentences) {
            for (String word : sentence) {
                writer.print(word + " ");
            }
            writer.println();
        }
    }

    @Override
    public void stopReporting() {
        unregister();
    }
}
