package com.oracle.coherence;

import com.hazelcast.util.WordUtil;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.net.cache.TypeAssertion;
import com.tangosol.util.InvocableMap;
import com.tangosol.util.ValueExtractor;
import com.tangosol.util.function.Remote;
import com.tangosol.util.stream.RemoteCollectors;
import com.tangosol.util.stream.RemoteStream;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.hazelcast.util.WordUtil.*;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * TODO
 *
 * @author Viktor Gamov on 3/16/17.
 *         Twitter: @gamussa
 * @since 0.0.1
 */
public class Starter {

    public static void main(String[] asArgs) throws Exception {
        System.setProperty("java.net.preferIPv4Stack", "true");
        //System.setProperty("tangosol.coherence.override", "tangosol-coherence-override.xml");
        System.setProperty("tangosol.coherence.cacheconfig", "cache-configuration.xml");

        NamedCache<Integer, String> source =
                CacheFactory.getCache("streams-source");

        //region loading war and peace
        System.out.println("Loading War and Peace...");
        fillMapWithData("war_and_peace_eng.txt", source);
        System.out.println("Done War and Peace...");
        //endregion


        ValueExtractor<String, String> ve1 = (ValueExtractor<String, String>) s -> s;
        ValueExtractor<Integer, Integer> ve2 = (ValueExtractor<Integer, Integer>) integer -> integer;

        // region j.u.s on Coherence
        final long start = System.nanoTime();
        final Map<String, Integer> collect = source.stream()
                .flatMap(m -> Stream.of(PATTERN.split(m.getValue())))
                .map(String::toLowerCase)
                .map(WordUtil::cleanWord)
                .filter(m -> m.length() >= 5)
                .collect(RemoteCollectors.toMap(ve1, ve2, Integer::sum));
        final long end = NANOSECONDS.toMillis(System.nanoTime() - start);
        System.out.println(end + " mills");
        System.out.println(collect);
        //endregion

        final NamedCache<String, Integer> counts = CacheFactory.getTypedCache("streams-count", TypeAssertion.withTypes(String.class, Integer.class));
        counts.putAll(collect);

        Remote.Predicate<InvocableMap.Entry<String, Integer>> p1 = (Remote.Predicate<InvocableMap.Entry<String, Integer>>) e -> Stream.of(EXCLUDES).noneMatch(s -> s.equals(e.getKey()));
        //region top20
        final RemoteStream<InvocableMap.Entry<String, Integer>> stream = counts.stream();
        /*final LinkedHashMap<Object, Integer> top20WordsMap = stream
                .map()
                .filter(p1)
                //.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                //.sorted()
                .limit(20)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (left, right) -> left,
                        LinkedHashMap::new));

        System.out.println("Counts=" + top20WordsMap.entrySet());*/
        //endregion


    }
}
