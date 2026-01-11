package com.auction.auction_backend.common.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class AppUtils {
    public static String removeAccent(String s) {
        if (s == null)
            return null;
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").toLowerCase().replace('đ', 'd');
    }

    public static String maskName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "****User";
        }
        String[] parts = fullName.trim().split("\\s+");
        // Get the last word (LastName)
        String lastName = (parts.length > 0) ? parts[parts.length - 1] : "User";
        // Make it strictly "****" + lastName.
        // If we want more uniqueness, we might need email. But usually name masking is
        // just last name.
        // The user's problem is likely that they used "Account 1" and "Account 2" which
        // might yield same last name if parsed poorly,
        // OR they are testing with "Nguyen A" and "Nguyen B" -> both "A" and "B" or
        // both "Nguyen"?
        // Let's assume the user wants differentiation.
        // Better strategy: "****" + last 2 chars of name + " " + last name?
        // Or just return "****" + LastName.

        // Let's stick to the current logic but make it consistent.
        return "****" + lastName;
    }
}
