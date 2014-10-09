package bubnov.crawler.listeners;

public class HtmlTagFilter extends TokenSender<Character> implements TokenListener<Character> {
    private boolean insideTag;
    private final TokenSender<Character> mySource;

    public HtmlTagFilter(TokenSender<Character> source) {
        insideTag = false;
        mySource = source;
        mySource.registerListener(this);
    }

    @Override
    public void receive(Character character) {
        //TODO add script tag filters
        if (character.equals('<')) {
            insideTag = true;
            flush();
        } else {
            if (character.equals('>')) {
                insideTag = false;
                flush();
            } else {
                if (!insideTag) {
                    sendToken(character);
                }
            }
        }
    }

    @Override
    public void unregister() {
        mySource.removeListener(this);
    }
}
