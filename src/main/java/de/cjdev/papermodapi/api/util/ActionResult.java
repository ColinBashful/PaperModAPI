package de.cjdev.papermodapi.api.util;

public enum ActionResult {
    SUCCESS,
    SUCCESS_NO_ITEM_USED,
    CONSUME,
    CONSUME_PARTIAL,
    PASS,
    FAIL;

    private ActionResult() {
    }

    public boolean isAccepted() {
        return this == SUCCESS || this == CONSUME || this == CONSUME_PARTIAL || this == SUCCESS_NO_ITEM_USED;
    }

    public boolean shouldSwingHand() {
        return this == SUCCESS || this == SUCCESS_NO_ITEM_USED;
    }

    public boolean shouldIncrementStat() {
        return this == SUCCESS || this == CONSUME;
    }

    public static ActionResult success(boolean swingHand) {
        return swingHand ? SUCCESS : CONSUME;
    }
}