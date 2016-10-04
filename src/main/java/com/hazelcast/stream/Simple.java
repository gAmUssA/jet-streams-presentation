package com.hazelcast.stream;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IList;

import java.util.Arrays;
import java.util.List;

import static com.hazelcast.core.Hazelcast.newHazelcastInstance;

/**
 * Created by vikgamov on 10/2/16.
 */
public class Simple {
    public static void main(String[] args) {

        List<String> myList =
                Arrays.asList("a1", "a2", "b1", "c2", "c1");

        final HazelcastInstance hazelcastInstance = newHazelcastInstance();
        final HazelcastInstance hazelcastInstance2 = newHazelcastInstance();
        final IList<String> iList = hazelcastInstance.getList("myList");
        iList.addAll(myList);

        iList.stream()
                .filter(s -> s.startsWith("c"))
                .map(String::toUpperCase)
                .sorted()
                .forEach(System.out::println);
    }
}
