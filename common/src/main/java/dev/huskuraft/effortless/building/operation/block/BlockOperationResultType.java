package dev.huskuraft.effortless.building.operation.block;

public enum BlockOperationResultType {
    SUCCESS,
    SUCCESS_PARTIAL,
    CONSUME,

    FAIL_WORLD_HEIGHT,
    FAIL_WORLD_BORDER,
    FAIL_WORLD_INCORRECT_DIM,
    FAIL_PLAYER_GAME_MODE,

    FAIL_PLACE_ITEM_INSUFFICIENT,
    FAIL_PLACE_ITEM_NOT_BLOCK,
    FAIL_BREAK_TOOL_INSUFFICIENT,
    FAIL_BREAK_REPLACE_RULE,
    FAIL_BREAK_REPLACE_FLAGS,
    FAIL_INTERACT_TOOL_INSUFFICIENT,

    FAIL_BREAK_BLACKLISTED,
    FAIL_PLACE_BLACKLISTED,
    FAIL_INTERACT_BLACKLISTED,
    FAIL_COPY_BLACKLISTED,

    FAIL_BREAK_NO_PERMISSION,
    FAIL_PLACE_NO_PERMISSION,
    FAIL_INTERACT_NO_PERMISSION,
    FAIL_COPY_NO_PERMISSION,

    FAIL_BLOCK_STATE_NULL,
    FAIL_BLOCK_STATE_AIR,
    FAIL_UNKNOWN;

    public boolean success() {
        return this == SUCCESS || this == SUCCESS_PARTIAL || this == CONSUME;
    }

    public boolean fail() {
        return !success();
    }
}
