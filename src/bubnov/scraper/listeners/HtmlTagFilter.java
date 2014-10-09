package bubnov.scraper.listeners;

public class HtmlTagFilter extends TokenSender<Character> implements TokenListener<Character> {
    private final TokenSender<Character> mySource;
    private boolean insideTag;

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
