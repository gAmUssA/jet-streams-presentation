/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.stream;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.jet.stream.Distributed;
import com.hazelcast.jet.stream.IStreamMap;
import com.hazelcast.util.WordUtil;

import java.util.Map;
import java.util.stream.Stream;

import static com.hazelcast.jet.stream.DistributedCollectors.toIMap;
import static com.hazelcast.util.WordUtil.*;

public class WordCountWithDistributedStreams {

    public static void main(String[] args) throws Exception {

        // two-nodes cluster
        //HazelcastInstance instance1 = Hazelcast.newHazelcastInstance();
        //HazelcastInstance instance2 = Hazelcast.newHazelcastInstance();
        final HazelcastInstance instance1 = HazelcastClient.newHazelcastClient();

        IMap<Integer, String> source = instance1.getMap("source");

        //region loading war and peace
        System.out.println("Loading War and Peace...");
        fillMapWithData("war_and_peace_eng.txt", source);
        System.out.println("Done War and Peace...");
        //endregion

        // Wrapping IMap with Distributed decorator
        IStreamMap<Integer, String> streamMap = IStreamMap.streamMap(source);

        //region word count
        IMap<String, Integer> counts = streamMap.stream()
                .flatMap(m -> Stream.of(PATTERN.split(m.getValue())))
                .map(String::toLowerCase)
                .map(WordUtil::cleanWord)
                .filter(m -> m.length() >= 5)
                .collect(toIMap(
                        m -> m,
                        m -> 1,
                        Integer::sum));
        //endregion

        System.out.println(counts.getName());
        //region top20
       /* final IList<String> top10Map =
                IStreamMap.streamMap(counts)
                        .stream()
                        .filter(e -> Stream.of(EXCLUDES).noneMatch(s -> s.equals(e.getKey())))
                        .sorted((o1, o2) -> Integer.compare(o2.getValue(), o1.getValue()))
                        .limit(20)
                        .map(e -> e.getKey() + " : " + e.getValue())
                        .collect(toIList());
*/
        final IMap<String, Integer> top10Map = IStreamMap.streamMap(counts)
                .stream()
                .filter(e -> Stream
                        .of(EXCLUDES)
                        .noneMatch(s -> s.equals(e.getKey())))
                .sorted((Distributed.Comparator<Map.Entry<String, Integer>>) (o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .limit(20)
                .collect(toIMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (left, right) -> left));
        System.out.println("Counts=" + top10Map);
        //endregion

        HazelcastClient.shutdownAll();

    }


}
