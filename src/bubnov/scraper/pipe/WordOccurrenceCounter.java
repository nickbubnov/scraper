package bubnov.scraper.pipe;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WordOccurrenceCounter implements TokenListener<String>, ReportProducer {
    private final Map<String, Integer> myOccurrenceCounter;
    private final TokenSender<String> mySource;

    public WordOccurrenceCounter(TokenSender<String> source, Collection<String> words) {
        mySource = source;
        mySource.registerListener(this);
        myOccurrenceCounter = new HashMap<String, Integer>();
        for (String word : words) {
            myOccurrenceCounter.put(word.toLowerCase(), 0);
        }
    }

    @Override
    public void receive(String word) {
        String lowerWord = word.toLowerCase();
        if (myOccurrenceCounter.containsKey(lowerWord)) {
            myOccurrenceCounter.put(lowerWord, myOccurrenceCounter.get(lowerWord) + 1);
        }
    }

    @Override
    public void flush() {}

    public Map<String, Integer> getWordCounts() {
        return myOccurrenceCounter;
    }

    @Override
    public void report(PrintWriter writer) {
        Map<String, Integer> wordCounts = getWordCounts();

        writer.println("Buzzword occurrences: ");
        for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
            writer.println(entry.getKey() + " : " + entry.getValue());
        }
        writer.println();
    }

    @Override
    public void unregister() {
        mySource.removeListener(this);
    }
}
