package com.jazzkuh.gunshell.common.configuration;

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
}
