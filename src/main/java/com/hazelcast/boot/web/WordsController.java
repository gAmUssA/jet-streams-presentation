package com.hazelcast.boot.web;

import com.hazelcast.boot.service.WordService;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.stream.IStreamMap;
import com.hazelcast.util.WordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by vikgamov on 4/3/17.
 */
@RestController
@RequestMapping("wordcount")
public class WordsController {

    private WordService wordService;
    private JetInstance jetInstance;

    @Autowired
    public WordsController(WordService wordService, JetInstance jetInstance) {
        this.wordService = wordService;
        this.jetInstance = jetInstance;
    }

    @RequestMapping(
            method = GET,
            path = "/{artist}")
    public String get(@PathVariable("artist") String artist) {
        final IStreamMap<Integer, String> artistMap = jetInstance.getMap(artist + WordUtil.SOURCE_SUFFIX);
        final IStreamMap<String, Integer> wordCount = wordService.wordCount(artistMap);
        return String.valueOf(wordCount.size());
    }

    @RequestMapping(
            method = GET,
            path = "/{artist}/top{x}")
    public List<String> getTopX(@PathVariable("artist") String artist, @PathVariable("x") int top) {
        final IStreamMap<String, Integer> map = jetInstance.getMap(artist + WordUtil.COUNTS_SOURCE);
        return wordService.topXWords(map, top);
    }
}
