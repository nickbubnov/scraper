package bubnov.scraper.listeners;

import bubnov.scraper.ReportProducer;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WordOccurrenceCounter implements TokenListener<String>, ReportProducer {
    private final Map<String, Integer> myOccurenceCounter;
    private final TokenSender<String> mySource;

    public WordOccurrenceCounter(TokenSender<String> source, Collection<String> words) {
        mySource = source;
        mySource.registerListener(this);
        myOccurenceCounter = new HashMap<String, Integer>();
        for (String word : words) {
            myOccurenceCounter.put(word.toLowerCase(), 0);
        }
    }

    @Override
    public void receive(String word) {
        String lowerWord = word.toLowerCase();
        if (myOccurenceCounter.containsKey(lowerWord)) {
            myOccurenceCounter.put(lowerWord, myOccurenceCounter.get(lowerWord) + 1);
        }
    }

    @Override
    public void flush() {}

    @Override
    public void unregister() {
        mySource.removeListener(this);
    }

    public Map<String, Integer> getWordCounts() {
        return myOccurenceCounter;
    }

    @Override
    public void report(PrintWriter writer) {
        Map<String, Integer> wordCounts = getWordCounts();

        for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
            writer.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    @Override
    public void stopReporting() {
        unregister();
    }
}