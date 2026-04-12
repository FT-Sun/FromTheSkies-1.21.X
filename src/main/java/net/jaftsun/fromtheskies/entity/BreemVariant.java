package net.jaftsun.fromtheskies.entity;

import java.util.Arrays;
import java.util.Comparator;

public enum BreemVariant {
    UNSET(0, 0x656161, 0xD97810),
    VILLAGER(1, 0x656161, 0xD97810),
    SOLDIER(2, 0x656161, 0x2A74BB),
    BRUTE(3, 0x656161, 0xE39B1F),
    SHAMAN(4, 0x656161, 0x63A8E5);

    private static final BreemVariant[] BY_ID = Arrays.stream(values())
            .sorted(Comparator.comparingInt(BreemVariant::getId))
            .toArray(BreemVariant[]::new);

    private final int id;
    private final int spawnEggBackgroundColor;
    private final int spawnEggHighlightColor;

    BreemVariant(int id, int spawnEggBackgroundColor, int spawnEggHighlightColor) {
        this.id = id;
        this.spawnEggBackgroundColor = spawnEggBackgroundColor;
        this.spawnEggHighlightColor = spawnEggHighlightColor;
    }

    public int getId() {
        return this.id;
    }

    public int getSpawnEggBackgroundColor() {
        return this.spawnEggBackgroundColor;
    }

    public int getSpawnEggHighlightColor() {
        return this.spawnEggHighlightColor;
    }

    public static BreemVariant byId(int id) {
        return BY_ID[Math.floorMod(id, BY_ID.length)];
    }
}
