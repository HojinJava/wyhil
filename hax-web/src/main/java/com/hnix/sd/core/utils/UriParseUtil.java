package com.hnix.sd.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UriParseUtil {

    public List<Map.Entry<String, String>> splitQuery(URL url) {
        return Pattern.compile("&")
                .splitAsStream(url.getQuery())
                .map(s -> Arrays.copyOf(s.split("=", 2), 2))
                .map(o -> Map.entry(decode(o[0]), decode(o[1])))
                .collect(Collectors.toList());
    }

    private static String decode(final String encoded) {
        return Optional.ofNullable(encoded)
                .map(e -> URLDecoder.decode(e, StandardCharsets.UTF_8))
                .orElse(null);
    }

}
