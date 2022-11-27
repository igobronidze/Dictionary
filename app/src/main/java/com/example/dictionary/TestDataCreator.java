package com.example.dictionary;

import java.util.ArrayList;
import java.util.List;

public class TestDataCreator {

    public static List<Word> getData() {
        List<Word> words = new ArrayList<>();
        words.add(new Word("obviously", "used to mean that a fact can easily be noticed or understood clearly", "ცხადია"));
        words.add(new Word("get rid of something", "to throw away or sell something because you don't want it any more", "მოშორება"));
        words.add(new Word("correlate", "if two or more facts, ideas erc correlate or if you correlate them, they are closely connected to each other or one causes others", "ურთიერთკავშირი"));
        words.add(new Word("enormous", "extremely large", "ძალიან დიდი"));
        words.add(new Word("bargain", "a) something on sale at a lower price than its true value  b) an agreement between two people or groups in which each promises to do something in exchange for something else", "ა) ფასდაკლება  ბ) შეთანხმება"));
        return words;
    }
}
