package com.hazelcast.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility class contains common methods for loading / cleaning data
 */
public class WordUtil {
    public static final Pattern PATTERN = Pattern.compile("\\W+");
    public static final String[] EXCLUDES = {"which", "would", "could", "that", "with", "were", "this", "what", "there", "from", "their", "those", "chorus"};

    public static final String SOURCE_SUFFIX = "_source";
    public static final String COUNTS_SOURCE = "_counts";

    private WordUtil() {
    }

    public static String cleanWord(String word) {
        return word.replaceAll("[^A-Za-zA-Яа-я]", "");
    }

    public static void fillMapWithData(String fileName, Map<Integer, String> map)
            throws Exception {


        InputStream is = WordUtil.class.getClassLoader().getResourceAsStream(fileName);
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(is));

        String line;
        Integer lineNum = 0;
        Map<Integer, String> localMap = new HashMap<>();
        while ((line = reader.readLine()) != null) {
            lineNum++;
            localMap.put(lineNum, line);
        }
        map.putAll(localMap);

        is.close();
        reader.close();
    }
}
