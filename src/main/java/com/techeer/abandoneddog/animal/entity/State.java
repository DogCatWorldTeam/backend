package com.techeer.abandoneddog.animal.entity;

public enum State {
    공고중("공고중"),
    보호중("보호중"),
    종료_자연사("종료(자연사)"),
    종료_안락사("종료(안락사)"),
    종료_반환("종료(반환)"),
    종료_입양("종료(입양)"),
    종료_기증("종료(기증)"),
    종료_방사("종료(방사)");

    private final String description;

    State(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static State fromDescription(String description) {
        for (State state : values()) {
            if (state.getDescription().equals(description)) {
                return state;
            }
        }
        throw new IllegalArgumentException("No constant with description " + description + " found");
    }
}
