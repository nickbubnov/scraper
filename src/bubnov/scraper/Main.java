package bubnov.scraper;

import bubnov.scraper.pipe.*;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            showUsage();
            return;
        }

        String dataSource = args[0];

        Collection<String> buzzWords = new ArrayList<String>();
        Collections.addAll(buzzWords, args[1].split(","));
        CommandFlags commandFlags = readCommandFlags(args);

        ScrapingStatus scrapingStatus = new ScrapingStatus();
        startScraping(commandFlags, scrapingStatus);

        InputStreamTokenizer inputStreamTokenizer = new InputStreamTokenizer();
        HtmlTagFilter charSource = new HtmlTagFilter(inputStreamTokenizer);
        WordTokenizer wordTokenizer = new WordTokenizer(charSource);
        SentenceTokenizer sentenceTokenizer = new SentenceTokenizer(wordTokenizer);

        Collection<ReportProducer> globalReporters = fillReporters(commandFlags, charSource, wordTokenizer, sentenceTokenizer, buzzWords);

        PrintWriter writer = new PrintWriter(System.out);
        Collection<String> dataSources;
        try {
            dataSources = fillDataSources(dataSource);
        } catch (Exception e) {
            writer.println("Unable to load urls from file");
            return;
        }

        for (String url : dataSources) {
            Collection<ReportProducer> localReporters = fillReporters(commandFlags, charSource, wordTokenizer, sentenceTokenizer, buzzWords);

            startUrlScraping(commandFlags, scrapingStatus);
            try {
                InputStream inputStream = readPage(url);
                inputStreamTokenizer.tokenizeStream(inputStream);
            } catch (IOException e) {
                //writer.println("Could not load " + url + " : " + e.getMessage());
                writer.println("Could not load " + url);
                writer.println();
                continue;
            }

            writer.println("URL STATS: " + url);
            for (ReportProducer producer : localReporters) {
                producer.report(writer);
                producer.unregister();
            }
            writer.println();
            endUrlScraping(writer, commandFlags, scrapingStatus);
            writer.flush();

        }

        writer.println("GLOBAL STATS: ");
        for (ReportProducer producer : globalReporters) {
            producer.report(writer);
            producer.unregister();
        }
        writer.println();
        endScraping(writer, commandFlags, scrapingStatus);
        writer.println();

        writer.flush();
    }

    private static void startScraping(CommandFlags commandFlags, ScrapingStatus scrapingStatus) {
        if (commandFlags.verbose) {
            scrapingStatus.globalScrapingStart = new Date();
        }
    }

    private static void startUrlScraping(CommandFlags commandFlags, ScrapingStatus scrapingStatus) {
        if (commandFlags.verbose) {
            scrapingStatus.urlScrapingStart = new Date();
        }
    }

    private static void endUrlScraping(PrintWriter writer, CommandFlags commandFlags, ScrapingStatus scrapingStatus) {
        if (commandFlags.verbose) {
            Date urlScrapingEnd = new Date();
            long duration = urlScrapingEnd.getTime() - scrapingStatus.urlScrapingStart.getTime();
            writer.println("Site parsed in " + duration + "ms");
            writer.println();
        }
    }

    private static void endScraping(PrintWriter writer, CommandFlags commandFlags, ScrapingStatus scrapingStatus) {
        if (commandFlags.verbose) {
            Date globalScrapingEnd = new Date();
            long duration = globalScrapingEnd.getTime() - scrapingStatus.globalScrapingStart.getTime();
            writer.println("Sources parsed in " + duration + "ms");
        }
    }


    private static Collection<ReportProducer> fillReporters(CommandFlags commandFlags, TokenSender<Character> charSource, TokenSender<String> wordSource, TokenSender<Collection<String>> sentenceSource, Collection<String> buzzWords) {
        Set<ReportProducer> reporters = new HashSet<ReportProducer>();
        if (commandFlags.countCharacters) {
            CharCounter charCounter = new CharCounter(charSource);
            reporters.add(charCounter);
        }
        if (commandFlags.countWordOccurrences) {
            WordOccurrenceCounter wordOccurrenceCounter = new WordOccurrenceCounter(wordSource, buzzWords);
            reporters.add(wordOccurrenceCounter);
        }
        if (commandFlags.extractSentences) {
            BuzzSentencesExtractor buzzSentencesExtractor = new BuzzSentencesExtractor(sentenceSource, buzzWords);
            reporters.add(buzzSentencesExtractor);
        }
        return reporters;
    }

    private static Collection<String> fillDataSources(String dataSource) throws Exception {
        Collection<String> urls = new ArrayList<String>();

        if (dataSource.startsWith("http")) {
            urls.add(dataSource);
        } else {
            urls.addAll(loadUrlsFromFile(dataSource));
        }

        return urls;
    }

    private static Collection<String> loadUrlsFromFile(String filename) throws Exception {
        Collection<String> urls = new ArrayList<String>();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
        String url;
        while ((url = bufferedReader.readLine()) != null) {
            urls.add(url);
        }
        bufferedReader.close();
        return urls;
    }

    public static InputStream readPage(String url) throws IOException {
        URL myURL = new URL(url);
        URLConnection myURLConnection = myURL.openConnection();
        myURLConnection.connect();
        return myURLConnection.getInputStream();
    }

    private static void showUsage() {
        System.out.println("Web Crawler");
        System.out.println("USAGE:");
        System.out.println("SOURCE - URL or address of file which contains URLS to be parsed");
        System.out.println("WORD[,WORD] - list of buzz words");
        System.out.println();
        System.out.println("Additional flags:");
        System.out.println("-v - verbose progress");
        System.out.println("-w - count buzz word occurrences");
        System.out.println("-c - count characters");
        System.out.println("-e - extract sentences which contain buzzwords");
    }

    private static CommandFlags readCommandFlags(String[] args) {
        CommandFlags commandFlags = new CommandFlags();
        for (int argNumber = 2; argNumber < args.length; argNumber++) {
            if (args[argNumber].equalsIgnoreCase("–v") || args[argNumber].equalsIgnoreCase("-v")) {
                commandFlags.verbose = true;
            }
            if (args[argNumber].equalsIgnoreCase("–w") || args[argNumber].equalsIgnoreCase("-w")) {
                commandFlags.countWordOccurrences = true;
            }
            if (args[argNumber].equalsIgnoreCase("–c") || args[argNumber].equalsIgnoreCase("-c")) {
                commandFlags.countCharacters = true;
            }
            if (args[argNumber].equalsIgnoreCase("–e") || args[argNumber].equalsIgnoreCase("-e")) {
                commandFlags.extractSentences = true;
            }
        }
        return commandFlags;
    }

    private static class CommandFlags {
        public boolean verbose = false;
        public boolean countWordOccurrences = false;
        public boolean countCharacters = false;
        public boolean extractSentences = false;
    }

    private static class ScrapingStatus {
        public Date globalScrapingStart;
        public Date urlScrapingStart;
    }
}
