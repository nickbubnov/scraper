package bubnov.crawler.listeners;

public class WordTokenizer extends TokenSender<String> implements TokenListener<Character> {
    private StringBuilder currentWord;
    private final TokenSender<Character> mySource;

    public WordTokenizer(TokenSender<Character> source) {
        currentWord = new StringBuilder();
        mySource = source;
        mySource.registerListener(this);
    }

    @Override
    public void receive(Character character) {
        char ch = character;
        if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
            currentWord.append(character);
        } else {
            if (currentWord.length() > 0) {
                sendToken(currentWord.toString());
                currentWord = new StringBuilder();
            }

            if (ch == '.') {
                sendToken(".");
            }
        }
    }

    @Override
    public void flush() {
        if (currentWord.length() > 0) {
            sendToken(currentWord.toString());
            currentWord = new StringBuilder();
        }

        super.flush();
    }

    @Override
    public void unregister() {
        mySource.removeListener(this);
    }
}
