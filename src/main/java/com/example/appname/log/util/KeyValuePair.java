package com.example.appname.log.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public final class KeyValuePair implements Serializable {

    private static final String EMPTY = "";

    private final String key;
    private final String value;

    public static KeyValuePair valueOf(String asString) {
        int equalsIndex = asString.indexOf('=');
        if (equalsIndex == -1)
            return new KeyValuePair(asString, null);

        String aKey = asString.substring(0, equalsIndex);
        String aValue = equalsIndex == asString.length() - 1 ? EMPTY : asString.substring(equalsIndex + 1);

        return new KeyValuePair(aKey, aValue);
    }
}
