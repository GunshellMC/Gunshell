package com.jazzkuh.lancaster.common.configuration;

import lombok.Getter;

public class PlaceHolder {
    private final String placeholder;
    private final @Getter String value;

    public PlaceHolder(String placeholder, String value) {
        this.placeholder = placeholder;
        this.value = value;
    }

    public String getPlaceholder() {
        return "<" + placeholder + ">";
    }

    public static String parse(String message, PlaceHolder... placeHolders) {
        for (PlaceHolder placeHolder : placeHolders) {
            if (message == null) continue;
            message = message.replaceAll(placeHolder.getPlaceholder(), placeHolder.getValue());
        }

        return message;
    }
}