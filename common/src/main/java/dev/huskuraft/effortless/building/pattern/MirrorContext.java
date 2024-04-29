package dev.huskuraft.effortless.building.pattern;

import dev.huskuraft.effortless.api.core.Axis;
import dev.huskuraft.effortless.api.core.BlockInteraction;
import dev.huskuraft.effortless.api.core.BlockPosition;
import dev.huskuraft.effortless.api.core.BlockState;
import dev.huskuraft.effortless.api.core.Orientation;
import dev.huskuraft.effortless.api.math.Vector3d;

public class MirrorContext {

    private final Vector3d center;
    private final Axis axis;

    private MirrorContext(Vector3d position, Axis axis) {
        this.center = position;
        this.axis = axis;
    }

    public static MirrorContext of(Vector3d position, Axis axis) {
        return new MirrorContext(position, axis);
    }

    public Vector3d mirror(Vector3d vec) {
        return switch (axis) {
            case Y -> new Vector3d(vec.x(), center.y() + (center.y() - vec.y()), vec.z());
            case X -> new Vector3d(center.x() + (center.x() - vec.x()), vec.y(), vec.z());
            case Z -> new Vector3d(vec.x(), vec.y(), center.z() + (center.z() - vec.z()));
        };
    }

    public BlockPosition mirror(BlockPosition blockPosition) {
        var vector = mirror(blockPosition.getCenter());
        return BlockPosition.at(vector);
    }

    public Orientation mirror(Orientation orientation) {
        return orientation.getAxis() != axis ? orientation : orientation.getOpposite();
    }

    public BlockInteraction mirror(BlockInteraction blockInteraction) {
        var direction = mirror(blockInteraction.getDirection());
        var position = mirror(blockInteraction.getPosition());
        var blockPosition = mirror(blockInteraction.getBlockPosition());
        return blockInteraction.withDirection(direction).withPosition(position).withBlockPosition(blockPosition);
    }

    public BlockState mirror(BlockState blockState) {
        if (blockState == null) {
            return blockState;
        }
        return blockState.mirror(axis);
    }

}
