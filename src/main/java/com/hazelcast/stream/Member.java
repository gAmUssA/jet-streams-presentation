package com.hazelcast.stream;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import static com.hazelcast.core.Hazelcast.newHazelcastInstance;
import static com.hazelcast.util.WordUtil.fillMapWithData;

/**
 * Created by vikgamov on 10/3/16.
 */
public class Member {
    public static void main(String[] args) throws Exception {
        final HazelcastInstance hazelcastInstance = newHazelcastInstance();
        final HazelcastInstance hazelcastInstance2 = newHazelcastInstance();
        IMap<Integer, String> source = hazelcastInstance.getMap("source");

        //region loading war and peace
        System.out.println("Loading War and Peace...");
        fillMapWithData("2600-0.txt", source);
        System.out.println("Done War and Peace...");
        //endregion
    }
}
