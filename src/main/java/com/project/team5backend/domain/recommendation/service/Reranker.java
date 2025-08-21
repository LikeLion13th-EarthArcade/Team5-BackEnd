package com.project.team5backend.domain.recommendation.service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Reranker {
    private Reranker() {}

    public static <T, ID, CAT, MOOD> List<T> enforceAtLeastNAndMatches(
            List<T> sortedByScoreDesc, // 점수 내림차순
            int k,                     // 상위 k개 (예: 4)
            int minAndCount,           // 최소 보장 개수 (예: 2)
            Function<T, ID> idFn,
            Function<T, CAT> catFn,
            Function<T, MOOD> moodFn,
            CAT topCat,
            MOOD topMood
    ) {
        int size = Math.min(k, sortedByScoreDesc.size());
        List<T> topK = new ArrayList<>(sortedByScoreDesc.subList(0, size));
        Set<ID> present = topK.stream().map(idFn).collect(Collectors.toSet());

        java.util.function.Predicate<T> AND = t ->
                Objects.equals(catFn.apply(t), topCat) &&
                        Objects.equals(moodFn.apply(t), topMood);

        long have = topK.stream().filter(AND).count();
        if (have >= minAndCount) return topK;

        List<T> andPool = sortedByScoreDesc.stream()
                .skip(size)                // topK 밖에서만 보충
                .filter(AND)
                .filter(t -> !present.contains(idFn.apply(t)))
                .toList();

        int poolIdx = 0;
        for (int i = size - 1; i >= 0 && have < minAndCount && poolIdx < andPool.size(); i--) {
            T cur = topK.get(i);
            if (!AND.test(cur)) {
                T cand = andPool.get(poolIdx++);
                if (present.add(idFn.apply(cand))) {
                    topK.set(i, cand);     // 뒤에서부터 교체 → 품질 손실 최소화
                    have++;
                }
            }
        }
        return topK; // 후보가 부족하면 가능한 만큼만 보장
    }
}