package com.hazelcast.stream;

import com.hazelcast.util.WordUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.hazelcast.util.WordUtil.PATTERN;
import static com.hazelcast.util.WordUtil.fillMapWithData;
import static java.util.stream.Collectors.toMap;

/**
 * Created by vikgamov on 10/3/16.
 */
public class WordCountWithStreams {

    public static void main(String[] args) throws Exception {

        Map<Integer, String> source = new HashMap<>();

        //region loading war and peace
        System.out.println("Loading War and Peace...");
        fillMapWithData("2600-0.txt", source);
        System.out.println("Done War and Peace...");
        //endregion

        final Set<Map.Entry<Integer, String>> streamMap = source.entrySet();

        //region word count
        Map<String, Integer> counts = streamMap.stream()
                .flatMap(m -> Stream.of(PATTERN.split(m.getValue())))
                .map(String::toLowerCase)
                .map(WordUtil::cleanWord)
                .filter(m -> m.length() >= 4)
                .collect(toMap(
                        key -> key,
                        value -> 1,
                        Integer::sum));
        //endregion

        //region top20
        String[] exclude = {"which", "would", "could", "that", "with", "were", "this", "what", "there", "from"};
        final Map<String, Integer> top10WordsMap = counts.entrySet().stream()
                .filter(e -> Stream.of(exclude).noneMatch(s -> s.equals(e.getKey())))
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(20)
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (left, right) -> left,
                        LinkedHashMap::new));

        System.out.println("Counts=" + top10WordsMap.entrySet());
        //endregion

    }

}

