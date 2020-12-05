package org.zadorozhn.building;

import lombok.Getter;
import lombok.EqualsAndHashCode;
import org.zadorozhn.building.state.Direction;

import static com.google.common.base.Preconditions.*;

@Getter
@EqualsAndHashCode
public class Call {
    private final int targetFloorNumber;
    private final Direction direction;

    private Call(int targetFloorNumber, Direction direction) {
        checkArgument(targetFloorNumber >= Floor.GROUND_FLOOR);
        checkNotNull(direction);

        this.targetFloorNumber = targetFloorNumber;
        this.direction = direction;
    }

    public static Call of(int targetFloorNumber, Direction direction) {
        return new Call(targetFloorNumber, direction);
    }

    public static Call of(Floor targetFloor, Direction direction) {
        checkNotNull(targetFloor);

        return new Call(targetFloor.getFloorNumber(), direction);
    }

    public static Call of(int targetFloorNumber, int startFloorNumber) {
        checkArgument(targetFloorNumber != startFloorNumber);

        Direction direction = resolveDirection(targetFloorNumber, startFloorNumber);

        return new Call(targetFloorNumber, direction);
    }

    public static Call of(Floor targetFloor, Floor startFloor) {
        checkNotNull(targetFloor);
        checkNotNull(startFloor);
        checkArgument(!targetFloor.equals(startFloor));

        Direction direction = resolveDirection(targetFloor.getFloorNumber(), startFloor.getFloorNumber());

        return new Call(targetFloor.getFloorNumber(), direction);
    }

    public static Call of(int targetFloorNumber, Floor startFloor) {
        checkNotNull(startFloor);
        checkArgument(targetFloorNumber != startFloor.getFloorNumber());

        Direction direction = resolveDirection(targetFloorNumber, startFloor.getFloorNumber());

        return new Call(targetFloorNumber, direction);
    }

    public static Call of(Floor targetFloor, int startFloorNumber) {
        checkNotNull(targetFloor);
        checkArgument(targetFloor.getFloorNumber() != startFloorNumber);

        Direction direction = resolveDirection(targetFloor.getFloorNumber(), startFloorNumber);

        return new Call(targetFloor.getFloorNumber(), direction);
    }

    private static Direction resolveDirection(int targetFloorNumber, int startFloorNumber) {
        return targetFloorNumber > startFloorNumber ? Direction.UP : Direction.DOWN;
    }

    @Override
    public String toString() {
        return String.format("(To->%S; Direction:%s)", targetFloorNumber, direction);
    }
}