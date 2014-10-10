package bubnov.scraper.pipe;

public class HtmlTagFilter extends TokenSender<Character> implements TokenListener<Character> {
    private boolean insideTag;

    public HtmlTagFilter(TokenSender<Character> source) {
        insideTag = false;
        source.registerListener(this);
    }

    @Override
    public void receive(Character character) {
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
}
