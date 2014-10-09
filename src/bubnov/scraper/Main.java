package bubnov.scraper;


import bubnov.scraper.listeners.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * Command line parameters example for Java implementation:
 * java –jar scraper.jar http://www.cnn.com Greece,default –v –w –c –e
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Args: ");
        for (String arg : args) {
            System.out.println(":" + arg);
        }

        if (args.length < 2) {
            showUsage();
            return;
        }

        String dataSource = args[0];

        Collection<String> buzzWords = new ArrayList<String>();

        //TODO check split
        Collections.addAll(buzzWords, args[1].split(","));

        boolean verbose = false;
        boolean countCharacters = false;
        boolean countWordOccurrences = false;
        boolean extractSentences = false;

        for (int argNumber = 2; argNumber < args.length; argNumber++) {
            //System.out.println("\"" + args[argNumber] + "\"");

            if (args[argNumber].equalsIgnoreCase("–v")) {
                System.out.println("Verbose");
                verbose = true;
            }

            if (args[argNumber].equalsIgnoreCase("–w")) {
                System.out.println("count word occurrence");
                countWordOccurrences = true;
            }
            if (args[argNumber].equalsIgnoreCase("–c")) {
                System.out.println("count chars");
                countCharacters = true;
            }
            if (args[argNumber].equalsIgnoreCase("–e")) {
                System.out.println("extract sentences");
                extractSentences = true;
            }
        }

        InputStreamTokenizer inputStreamTokenizer = new InputStreamTokenizer();
        HtmlTagFilter charSource = new HtmlTagFilter(inputStreamTokenizer);
        WordTokenizer wordTokenizer = new WordTokenizer(charSource);
        SentenceTokenizer sentenceTokenizer = new SentenceTokenizer(wordTokenizer);

        Collection<ReportProducer> globalReporters = new HashSet<ReportProducer>();
        addCharCounter(countCharacters, charSource, globalReporters);
        addWordOccurrenceCounter(countWordOccurrences, wordTokenizer, globalReporters, buzzWords);
        addBuzzSentenceListener(extractSentences, sentenceTokenizer, globalReporters, buzzWords);

        Collection<String> dataSources = fillDataSources(dataSource);

        PrintWriter writer = new PrintWriter(System.out);
        for (String url : dataSources) {
            Collection<ReportProducer> localReporters = new HashSet<ReportProducer>();
            addCharCounter(countCharacters, charSource, localReporters);
            addWordOccurrenceCounter(countWordOccurrences, wordTokenizer, localReporters, buzzWords);
            addBuzzSentenceListener(extractSentences, sentenceTokenizer, localReporters, buzzWords);

            try {
                InputStream inputStream = WebReader.readPage(url);
                inputStreamTokenizer.tokenizeStream(inputStream);
            } catch (IOException e) {
                writer.println("Exception while handling url " + url + " " + e.getMessage());
                continue;
            }

            writer.println("URL STATS: " + url);
            for (ReportProducer producer : localReporters) {
                producer.report(writer);
                producer.stopReporting();
            }
            writer.println();
        }

        writer.println("Global stats: ");
        for (ReportProducer producer : globalReporters) {
            producer.report(writer);
            producer.stopReporting();
        }
        writer.println("");
        writer.flush();

        sentenceTokenizer.unregister();
        wordTokenizer.unregister();
        charSource.unregister();
    }

    private static Collection<String> fillDataSources(String dataSource) {
        //TODO
        Collection<String> urls = new ArrayList<String>();
        urls.add(dataSource);
        return urls;
    }

    private static void addBuzzSentenceListener(boolean extractSentences, TokenSender<Collection<String>> tokenSender, Collection<ReportProducer> reporters, Collection<String> buzzWords) {
        if (extractSentences) {
            BuzzSentenceListener buzzSentenceListener = new BuzzSentenceListener(tokenSender, buzzWords);
            reporters.add(buzzSentenceListener);
        }
    }

    private static void addWordOccurrenceCounter(boolean countWordOccurrences, TokenSender<String> wordSender, Collection<ReportProducer> reporters, Collection<String> buzzWords) {
        if (countWordOccurrences) {
            WordOccurrenceCounter wordOccurrenceCounter = new WordOccurrenceCounter(wordSender, buzzWords);
            reporters.add(wordOccurrenceCounter);
        }
    }

    private static void addCharCounter(boolean countCharacters, TokenSender<Character> charSource, Collection<ReportProducer> reporters) {
        if (countCharacters) {
            CharCounter charCounter = new CharCounter(charSource);
            reporters.add(charCounter);
        }
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
}
