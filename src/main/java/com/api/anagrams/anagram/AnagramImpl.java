package com.api.anagrams.anagram;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Anagram implementation.
 *
 */
@Service
public class AnagramImpl {

    public static String Anagram(String a, String b) {

        if (a == null || b == null || a.equals("") || b.equals(""))
            throw new IllegalArgumentException();
        if (a.length() != b.length())
            return null;

        a = a.toLowerCase();
        b = b.toLowerCase();

        Map<Character, Integer> map = new HashMap<>();

        for (int k = 0; k < b.length(); k++) {
            char letter = b.charAt(k);

            if (!map.containsKey(letter)) {
                map.put(letter, 1);
            } else {
                Integer frequency = map.get(letter);
                map.put(letter, ++frequency);
            }
        }

        for (int k = 0; k < a.length(); k++) {
            char letter = a.charAt(k);

            if (!map.containsKey(letter))
                return null;

            Integer frequency = map.get(letter);

            if (frequency == 0)
                return null;
            else
                map.put(letter, --frequency);
        }

        return b;
    }
}
