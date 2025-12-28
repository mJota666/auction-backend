package com.auction.auction_backend.common.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class AppUtils {
    public static String removeAccent(String s) {
        if (s == null) return null;
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").toLowerCase().replace('đ', 'd');
    }
}
