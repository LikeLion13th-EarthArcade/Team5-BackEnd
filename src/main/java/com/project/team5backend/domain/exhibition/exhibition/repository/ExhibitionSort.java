package com.project.team5backend.domain.exhibition.exhibition.repository;


public enum ExhibitionSort {
    NEW,      // 최신순
    OLD,      // 오래된 순
    POPULAR;  // 인기순

    public static ExhibitionSort from(String s) {
        if (s == null || s.isBlank()) return NEW;
        return valueOf(s.toUpperCase());
    }
}
