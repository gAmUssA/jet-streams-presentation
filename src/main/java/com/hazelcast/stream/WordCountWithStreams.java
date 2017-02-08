package com.hazelcast.stream;

import com.hazelcast.util.WordUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.hazelcast.util.WordUtil.*;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.stream.Collectors.toMap;

/**
 * Word count using Java 8 Streams + parallelStream
 */
public class WordCountWithStreams {

    public static void main(String[] args) throws Exception {

        Map<Integer, String> source = new HashMap<>();

        //region loading war and peace
        System.out.println("Loading War and Peace...");
        fillMapWithData("war_and_peace_eng.txt", source);
        System.out.println("Done War and Peace...");
        //endregion

        //region word count
        final Set<Map.Entry<Integer, String>> streamMap = source.entrySet();
        final long start = System.nanoTime();
        Map<String, Integer> counts = streamMap.stream()
                .flatMap(m -> Stream.of(PATTERN.split(m.getValue())))
                .map(String::toLowerCase)
                .map(WordUtil::cleanWord)
                .filter(m -> m.length() >= 5)
                .collect(toMap(
                        key -> key,
                        value -> 1,
                        Integer::sum));
        final long end = NANOSECONDS.toMillis(System.nanoTime() - start);
        System.out.println(end + " mills");
        //endregion

        //region top20
        final Map<String, Integer> top20WordsMap = counts.entrySet().stream()
                .filter(e -> Stream.of(EXCLUDES).noneMatch(s -> s.equals(e.getKey())))
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(20)
                .collect(toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (left, right) -> left,
                        LinkedHashMap::new));

        System.out.println("Counts=" + top20WordsMap.entrySet());
        //endregion

    }

}

