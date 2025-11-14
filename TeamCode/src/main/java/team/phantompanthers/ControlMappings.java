package team.phantompanthers;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.function.Function;

public enum ControlMappings {
    MOVEMENT_X((g) -> g.left_stick_x),
    MOVEMENT_Y((g) -> g.left_stick_y),
    ROTATION((g) -> g.right_trigger - g.left_trigger),
    INTAKE((g) -> {
        if (g.x && g.y)
            return 0;
        if (g.x)
            return 1;
        if (g.y)
            return -1;

        return 0;
    }),
    KICK((g) -> {
        if (g.dpad_up && g.dpad_down)
            return 0;
        if (g.dpad_up)
            return 1;
        if (g.dpad_down)
            return -1;

        return 0;
    }),
    SPIN((g) -> {
        if (g.right_bumper && g.left_bumper)
            return 0;
        if (g.right_bumper)
            return 1;
        if (g.left_bumper)
            return -1;

        return 0;
    });

    private final Function<Gamepad, Object> valueProvider;

    ControlMappings(Function<Gamepad, Object> valueProvider) {
        this.valueProvider = valueProvider;
    }

    public <T> T get(Class<T> clazz, Gamepad gamepad) {
        Object rawValue = valueProvider.apply(gamepad);

        return clazz.cast(rawValue);
    }

    public float getFloatCubic(Gamepad gamepad) {
        return (float) Math.pow(get(Double.class, gamepad), 3);
    }
}