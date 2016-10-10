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

package com.hazelcast.jet;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.jet.sink.MapSink;
import com.hazelcast.jet.source.MapSource;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.WordUtil;


/**
 * A distributed word count can be implemented with three vertices as follows:
 * -------------              ---------------                         ------------
 * | Generator |-(word, 1)--> | Accumulator | -(word, localCount)--> | Combiner  | --(word, globalCount) ->
 * -------------              ---------------                         ------------
 * <p>
 * first vertex will be split the words in each paragraph and emit tuples as (WORD, 1)
 * second vertex will combine the counts locally on each node
 * third vertex will combine the counts across all nodes.
 * <p>
 * The edge between generator and accumulator is local, but partitioned, so that all words with same hash go
 * to the same instance of the processor on the same node.
 * <p>
 * The edge between the accumulator and combiner vertex is both shuffled and partitioned, meaning all words
 * with same hash are processed by the same instance of the processor across all nodes.
 */
public class JetWordCount {

    private static final ILogger LOGGER = Logger.getLogger(JetWordCount.class);

    public static void main(String[] args) throws Exception {
        HazelcastInstance instance1 = Hazelcast.newHazelcastInstance();
        HazelcastInstance instance2 = Hazelcast.newHazelcastInstance();

        IMap<Integer, String> source = instance1.getMap("source");
        IMap<String, Integer> sink = instance1.getMap("sink");

        System.out.println("Loading War and Peace...");
        WordUtil.fillMapWithData("war_and_peace_eng.txt", source);
        System.out.println("Done War and Peace...");

        DAG dag = new DAG();

        int parallelism = Runtime.getRuntime().availableProcessors();

        Vertex generator = new Vertex("word-generator", WordGeneratorProcessor.class)
                .parallelism(parallelism);

        generator.addSource(new MapSource(source));

        Vertex accumulator = new Vertex("word-accumulator", WordCombinerProcessor.class)
                .parallelism(parallelism);

        Vertex combiner = new Vertex("word-combiner", WordCombinerProcessor.class)
                .parallelism(parallelism);

        combiner.addSink(new MapSink(sink));

        dag.addVertex(generator);
        dag.addVertex(accumulator);
        dag.addVertex(combiner);

        // use partitioning to ensure the same words are consumed by the same processor instance
        dag.addEdge(new Edge("generator-accumulator", generator, accumulator)
                .partitioned());

        dag.addEdge(new Edge("accumulator-combiner", accumulator, combiner)
                .partitioned()
                .distributed()
        );

        LOGGER.info("Submitting DAG");
        Job job = JetEngine.getJob(instance1, "word-count", dag);
        try {
            LOGGER.info("Executing application");
            job.execute().get();
            LOGGER.info("Counts=" + sink.entrySet().toString());
        } finally {
            job.destroy();
            // Hazelcast.shutdownAll();
        }
    }
}
