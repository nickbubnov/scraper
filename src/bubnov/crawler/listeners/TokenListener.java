package bubnov.crawler.listeners;


public interface TokenListener<T> {
    public void receive(T t);
    public void flush();
    public void unregister();
}
