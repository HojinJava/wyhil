package com.hnix.sd.common.excel.util;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ExcelFileNames {

    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyMMdd");

    public static String createExcelFileName(String prefix) throws UnsupportedEncodingException {
        LocalDate now = LocalDate.now();

        String currentDate = now.format(format);

        return StringUtils.isEmpty(prefix)
                ? String.format("HAX-WEB_%s", currentDate)
                : encodeFileName(String.format("%s_%s", prefix, currentDate));
    }

    private static String encodeFileName(String originFileName) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();

        char[] fileChars = originFileName.toCharArray();

        for (char ch : fileChars) {
            sb.append(ch > '~' ? URLEncoder.encode(String.valueOf(ch), "UTF-8") : ch);
        }

        return sb.toString();
    }

}
