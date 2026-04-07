package net.jaftsun.fromtheskies.entity;

import java.util.Arrays;
import java.util.Comparator;

public enum BreemVariant {
    VILLAGER(0),
    SOLDIER(1),
    BRUTE(2),
    SHAMAN(3);

    private static final BreemVariant[] BY_ID = Arrays.stream(values())
            .sorted(Comparator.comparingInt(BreemVariant::getId))
            .toArray(BreemVariant[]::new);

    private final int id;

    BreemVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static BreemVariant byId(int id) {
        return BY_ID[Math.floorMod(id, BY_ID.length)];
    }
}
