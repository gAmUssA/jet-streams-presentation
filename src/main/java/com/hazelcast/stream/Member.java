package com.hazelcast.stream;

import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.config.JetConfig;
import com.hazelcast.jet.stream.IStreamMap;

import java.util.Map;

import static com.hazelcast.util.WordUtil.SOURCE_SUFFIX;
import static com.hazelcast.util.WordUtil.fillMapWithData;

public class Member {
    public static void main(String[] args) throws Exception {
        JetConfig jetConfig = new JetConfig();
        jetConfig.setHazelcastConfig(new XmlConfigBuilder().build());

        final JetInstance instance = Jet.newJetInstance(jetConfig);
        Jet.newJetInstance(jetConfig);

        //IMap<Integer, String> source = instance.getMap("source");

        final Map<Integer, String> disturbedMap = instance.getMap("disturbed" + SOURCE_SUFFIX);
        final Map<Integer, String> gagaMap = instance.getMap("gaga" + SOURCE_SUFFIX);
        fillMapWithData("disturbed.txt", disturbedMap);
        fillMapWithData("lady_gaga.txt", gagaMap);

        //region loading war and peace
        //System.out.println("Loading War and Peace...");
        //fillMapWithData("war_and_peace_eng.txt", source);
        //System.out.println("Done War and Peace...");
        //endregion

    }
}
