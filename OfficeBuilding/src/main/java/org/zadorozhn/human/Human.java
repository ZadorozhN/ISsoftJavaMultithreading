package org.zadorozhn.human;

import lombok.*;
import java.util.UUID;
import org.zadorozhn.building.Call;
import org.zadorozhn.building.Floor;
import org.zadorozhn.building.state.Direction;

import static com.google.common.base.Preconditions.*;

@Getter
@EqualsAndHashCode
@ToString
public class Human {
    public static final int MIN_WEIGHT = 10;
    public static final int MAX_WEIGHT = 200;

    private final UUID ssn;
    private final int weight;
    private final Call call;
    private final Floor startFloor;

    private Human(int weight, int targetFloorNumber, Floor startFloor) {
        checkNotNull(startFloor);
        checkArgument(targetFloorNumber >= Floor.GROUND_FLOOR);
        checkArgument(targetFloorNumber != startFloor.getFloorNumber());
        checkArgument(weight >= MIN_WEIGHT && weight <= MAX_WEIGHT);

        this.startFloor = startFloor;
        this.ssn = UUID.randomUUID();
        this.weight = weight;
        this.call = Call.of(targetFloorNumber,
                targetFloorNumber - startFloor.getFloorNumber() > 0 ? Direction.UP : Direction.DOWN);
    }

    public static Human of(int weight, int targetFloorNumber, Floor startFloor) {
        return new Human(weight, targetFloorNumber, startFloor);
    }

    public static Human of(int weight, Floor targetFloor, Floor startFloor) {
        checkNotNull(targetFloor);
        checkArgument(!targetFloor.equals(startFloor));

        return new Human(weight, targetFloor.getFloorNumber(), startFloor);
    }

    public void pushButton() {
        startFloor.callElevator(call.getDirection());
    }
}