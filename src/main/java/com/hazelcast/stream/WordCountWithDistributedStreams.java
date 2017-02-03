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

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.IMap;
import com.hazelcast.jet.Distributed;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JetConfig;
import com.hazelcast.jet.stream.IStreamMap;
import com.hazelcast.util.WordUtil;

import java.util.Map;
import java.util.stream.Stream;

import static com.hazelcast.jet.stream.DistributedCollectors.toIMap;
import static com.hazelcast.util.WordUtil.EXCLUDES;
import static com.hazelcast.util.WordUtil.PATTERN;

public class WordCountWithDistributedStreams {

    public static void main(String[] args) throws Exception {

        //region init Jet Engine

        JetConfig c = new JetConfig();
        ClientConfig cc = new ClientConfig();
        final JetInstance jetInstance = Jet.newJetClient(cc);
        final IStreamMap<Integer, String> streamMap = jetInstance.getMap("source");
        //endregion

        //region word count
        IStreamMap<String, Integer> counts = streamMap.stream()
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

        final IMap<String, Integer> top20Map = counts.stream()
                .filter(e -> Stream
                        .of(EXCLUDES)
                        .noneMatch(s -> s.equals(e.getKey())))
                .sorted((Distributed.Comparator<Map.Entry<String, Integer>>) (o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .limit(20)
                .collect(toIMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (left, right) -> left));

        System.out.println("Counts=" + top20Map);
        //endregion

        //  HazelcastClient.shutdownAll();

    }


}
