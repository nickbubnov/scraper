package bubnov.crawler.listeners;


import java.util.HashSet;
import java.util.Set;

public abstract class TokenSender<T> {
    private final Set<TokenListener<T>> myListeners = new HashSet<TokenListener<T>>();

    public void registerListener(TokenListener<T> listener) {
        myListeners.add(listener);
    }

    public void removeListener(TokenListener<T> listener) {
        myListeners.remove(listener);
    }

    protected void sendToken(T t) {
        for (TokenListener<T> listener : myListeners) {
            listener.receive(t);
        }
    }

    public void flush() {
        for (TokenListener<T> listener : myListeners) {
            listener.flush();
        }
    }
}
