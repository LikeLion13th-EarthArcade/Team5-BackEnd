package com.project.team5backend.global.entity;

import lombok.Getter;

@Getter
public enum Facility {
    RESTROOM("화장실"),
    WIFI("와이파이"),;

    private final String description;

    Facility(String description) {
        this.description = description;
    }
}
