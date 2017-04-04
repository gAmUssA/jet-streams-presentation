package com.hazelcast.boot.service;

import com.hazelcast.boot.config.AppConfig;
import com.hazelcast.boot.config.HazelcastJetConfig;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.stream.IStreamMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {HazelcastJetConfig.class, AppConfig.class})
public class WordServiceTest {
    @Autowired
    private JetInstance jet;

    @Autowired
    WordService ws;

    private IStreamMap<Integer, String> source;
    private IStreamMap<String, Integer> counts;

    @Before
    public void setUp() throws Exception {
        ws = new WordService();
        source = jet.getMap("source");
        counts = ws.wordCount(source);
    }

    @Test
    public void topXWords() throws Exception {
        final List<String> stringList = ws.topXWords(counts, 10);
        System.out.println(stringList);
        Assert.assertEquals(10, stringList.size());
    }

}