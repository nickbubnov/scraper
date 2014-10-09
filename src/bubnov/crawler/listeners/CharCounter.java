package bubnov.crawler.listeners;


import bubnov.crawler.ReportProducer;

import java.io.PrintWriter;

public class CharCounter implements TokenListener<Character>, ReportProducer {
    private int myCounter;
    private final TokenSender<Character> mySource;

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
    }

    @Override
    public void stopReporting() {
        unregister();
    }
}
