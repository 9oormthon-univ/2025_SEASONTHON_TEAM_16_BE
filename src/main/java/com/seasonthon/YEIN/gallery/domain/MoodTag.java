package com.seasonthon.YEIN.gallery.domain;

import lombok.Getter;

@Getter
public enum MoodTag {
    CALM("평온"),
    FOCUSED("집중"),
    DEPRESSED("우울");

    private final String description;

    MoodTag(String description) {
        this.description = description;
    }
}
