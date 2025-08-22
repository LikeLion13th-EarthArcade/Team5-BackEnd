package com.project.team5backend.domain.recommendation.model;

public enum ActionType {
    CLICK(1.0),
    LIKE(1.6);

    private final double weight;
    ActionType(double w){ this.weight = w; }
    public double weight(){ return weight; }
}
