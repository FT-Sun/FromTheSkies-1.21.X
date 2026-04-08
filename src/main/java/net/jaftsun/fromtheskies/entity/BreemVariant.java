package net.jaftsun.fromtheskies.entity;

import java.util.Arrays;
import java.util.Comparator;

public enum BreemVariant {
    UNSET(0),
    VILLAGER(1),
    SOLDIER(2),
    BRUTE(3),
    SHAMAN(4);

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
