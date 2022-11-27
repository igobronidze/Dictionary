package com.example.dictionary.processor;

import com.example.dictionary.Word;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WordsDataProcessor {

    private static final String DELIMITER_FOR_READ = "\\|";
    private static final String DELIMITER_FOR_WRITE = "|";

    private static final String SPACES = "   ";

    public static List<Word> read(File file) {
        List<Word> words = new ArrayList<>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String line = bufferedReader.readLine();
            while (line != null && !line.isEmpty()) {
                words.add(unmarshallWord(line));

                line = bufferedReader.readLine();
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return words;
    }

    public static void save(List<Word> words, File file) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (Word word : words) {
                String line = marshallWord(word);
                bufferedWriter.write(line + "\n");
            }

            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String marshallWord(Word word) {
        String line = "";

        line += word.getWord() + SPACES + DELIMITER_FOR_WRITE + SPACES;
        line += word.getExplain() + SPACES + DELIMITER_FOR_WRITE + SPACES;
        line += word.getTranslate() + SPACES + DELIMITER_FOR_WRITE + SPACES;
        line += word.getRank() + SPACES + DELIMITER_FOR_WRITE + SPACES;
        line += word.getTotalShows();

        return line;
    }

    private static Word unmarshallWord(String line) {
        String[] arr = line.split(DELIMITER_FOR_READ);

        Word word = new Word();
        word.setWord(arr[0].trim());
        word.setExplain(arr[1].trim());
        word.setTranslate(arr[2].trim());
        if (arr.length > 3) {
            word.setRank(Integer.parseInt(arr[3].trim()));
        }
        if (arr.length > 4) {
            word.setTotalShows(Integer.parseInt(arr[4].trim()));
        } else {
            word.setTotalShows(word.getRank());
        }

        return word;
    }
}
