package bubnov.scraper.pipe;


import java.io.PrintWriter;

public interface ReportProducer {
    public void report(PrintWriter writer);
    public void unregister();
}
