package net.jaftsun.fromtheskies.takeover.data;

import java.util.Objects;

import net.jaftsun.fromtheskies.takeover.TakeoverLifecycleState;

public class TakeoverSavedData {
    private TakeoverLifecycleState state = TakeoverLifecycleState.DORMANT;

    public TakeoverLifecycleState getState() {
        return this.state;
    }

    public void setState(TakeoverLifecycleState state) {
        this.state = Objects.requireNonNull(state, "state");
    }
}
