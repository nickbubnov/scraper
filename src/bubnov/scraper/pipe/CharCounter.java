package bubnov.scraper.pipe;


import java.io.PrintWriter;

public class CharCounter implements TokenListener<Character>, ReportProducer {
    private final TokenSender<Character> mySource;
    private int myCounter;

    public CharCounter(TokenSender<Character> source) {
        mySource = source;
        mySource.registerListener(this);
        myCounter = 0;
    }

    @Override
    public void receive(Character character) {
        myCounter++;
    }

    @Override
    public void flush() {}

    @Override
    public void unregister() {
        mySource.removeListener(this);
    }

    public int getCounter() {
        return myCounter;
    }

    @Override
    public void report(PrintWriter writer) {
        int charCounter = getCounter();
        writer.println("Char count: " + charCounter);
        writer.println();
    }
}
