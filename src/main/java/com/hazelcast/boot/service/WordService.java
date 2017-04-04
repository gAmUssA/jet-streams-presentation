package com.hazelcast.boot.service;

import com.hazelcast.jet.stream.IStreamMap;
import com.hazelcast.util.WordUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.hazelcast.jet.stream.DistributedCollectors.toIMap;
import static com.hazelcast.jet.stream.DistributedCollectors.toList;
import static com.hazelcast.util.WordUtil.*;

@Service
public class WordService {

    public IStreamMap<String, Integer> wordCount(IStreamMap<Integer, String> source) {
        final String artist = source.getName().split("_")[0];
        return source.stream()
                .flatMap(m -> Stream.of(PATTERN.split(m.getValue())))
                .map(String::toLowerCase)
                .map(WordUtil::cleanWord)
                .filter(m -> m.length() >= 5)
                .collect(toIMap(
                        artist + COUNTS_SOURCE,
                        m -> m,
                        m -> 1,
                        Integer::sum));
    }

    public List<String> topXWords(IStreamMap<String, Integer> wordCount, int top) {
        return wordCount.stream()
                .filter(e -> Stream
                        .of(EXCLUDES)
                        .noneMatch(s -> s.equals(e.getKey())))
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                //.sorted((o1, o2) -> compare(o1.getValue(), o2.getValue()))
                .limit(top)
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(toList());
    }
}
