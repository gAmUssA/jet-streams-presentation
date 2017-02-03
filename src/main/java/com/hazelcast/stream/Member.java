package com.hazelcast.stream;

import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JetConfig;

import static com.hazelcast.core.Hazelcast.newHazelcastInstance;
import static com.hazelcast.util.WordUtil.fillMapWithData;

/**
 * Created by vikgamov on 10/3/16.
 */
public class Member {
    public static void main(String[] args) throws Exception {
        JetConfig jetConfig = new JetConfig();
        jetConfig.setHazelcastConfig(new XmlConfigBuilder().build());

        final JetInstance instance = Jet.newJetInstance(jetConfig);
        Jet.newJetInstance(jetConfig);

        IMap<Integer, String> source = instance.getMap("source");

        //region loading war and peace
        System.out.println("Loading War and Peace...");
        fillMapWithData("war_and_peace_eng.txt", source);
        System.out.println("Done War and Peace...");
        //endregion
    }
}
