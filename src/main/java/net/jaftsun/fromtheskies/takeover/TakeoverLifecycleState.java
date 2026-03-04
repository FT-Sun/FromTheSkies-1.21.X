package net.jaftsun.fromtheskies.takeover;

/**
 * High-level takeover progression persisted in {@code TakeoverSavedData}.
 */
public enum TakeoverLifecycleState {
    // Default world state before generated-chunk threshold is reached.
    DORMANT,
    // Threshold met; scheduler is waiting to trigger the meteor/core selection.
    ARMED,
    // Core has landed and surface spread is active.
    ACTIVE,
    // Core was destroyed; no new spread is allowed.
    STOPPED,
    // Reserved for a future full lifecycle completion state.
    COMPLETED
}
