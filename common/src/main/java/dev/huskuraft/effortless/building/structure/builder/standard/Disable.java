package dev.huskuraft.effortless.building.structure.builder.standard;

import java.util.stream.Stream;

import dev.huskuraft.effortless.api.core.BlockInteraction;
import dev.huskuraft.effortless.api.core.BlockPosition;
import dev.huskuraft.effortless.api.core.Player;
import dev.huskuraft.effortless.building.Context;
import dev.huskuraft.effortless.building.structure.BuildMode;
import dev.huskuraft.effortless.building.structure.builder.BlockStructure;

public record Disable() implements BlockStructure {

    public BlockInteraction trace(Player player, Context context, int index) {
        return null;
    }

    public Stream<BlockPosition> collect(Context context, int index) {
        return Stream.empty();
    }

    @Override
    public int traceSize(Context context) {
        return 1;
    }

    @Override
    public BuildMode getMode() {
        return BuildMode.DISABLED;
    }
}
