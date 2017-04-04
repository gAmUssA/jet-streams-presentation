package com.hazelcast.stream;

import java.util.HashMap;
import java.util.Map;

import static com.hazelcast.util.WordUtil.*;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * Simple and naïve version of word count.
 * Uses `map.compute`
 */
public class Simple {
    public static void main(String[] args) throws Exception {

        Map<String, Long> counts = new HashMap<>();
        Map<Integer, String> source = new HashMap<>();

        //region loading war and peace
        System.out.println("Loading War and Peace...");
        fillMapWithData("war_and_peace_eng.txt", source);
        System.out.println("Done War and Peace...");
        //endregion

        final long start = System.nanoTime();
        for (String line : source.values()) {
            for (String word : PATTERN.split(line)) {
                if (word.length() >= 5)
                    counts.compute(
                            cleanWord(word).toLowerCase(),
                            (w, c) -> c == null ? 1L : c + 1
                    );
            }
        }
        final long end = NANOSECONDS.toMillis(System.nanoTime() - start);
        System.out.println(end + " mills");
        System.out.println(counts);
    }
}
