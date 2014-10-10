package bubnov.scraper.pipe;


public interface TokenListener<T> {
    public void receive(T t);
    public void flush();
}
