package team.phantompanthers;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.function.Function;

public enum ControlMappings {
    MOVEMENT_X((g) -> g.left_stick_x),
    MOVEMENT_Y((g) -> g.left_stick_y),
    ROTATION((g) -> g.right_trigger - g.left_trigger),
    INTAKE((g) -> {
        if (g.right_bumper && g.left_bumper)
            return 0;
        if (g.right_bumper)
            return 1;
        if (g.left_bumper)
            return -1;

        if (g.dpad_left && g.dpad_right)
            return 0;
        if (g.dpad_right)
            return 1;
        if (g.dpad_left)
            return -1;

        return 0;
    }),
    WHEEL_SPIN((g) -> {
        if (g.right_bumper && g.left_bumper)
            return 0;
        if (g.right_bumper)
            return .4;
        if (g.left_bumper)
            return -.4;

        return 0;
    }),
    LAUNCHER_SPIN((g) -> {
        if (g.y)
            return -1;

        return 0;
    }),
    SPIN_TO_PURPLE(Gamepad::bWasPressed),
    SPIN_TO_GREEN(Gamepad::aWasPressed),
    FIND_CANCEL(Gamepad::xWasPressed),
    LAUNCH(Gamepad::dpadUpWasPressed);

    private final Function<Gamepad, Object> valueProvider;

    ControlMappings(Function<Gamepad, Object> valueProvider) {
        this.valueProvider = valueProvider;
    }

    @SuppressWarnings("unchecked")
    public static <T> T convertNumber(Number value, Class<T> target) {
        if (target == Byte.class)    return (T) Byte.valueOf(value.byteValue());
        if (target == Short.class)   return (T) Short.valueOf(value.shortValue());
        if (target == Integer.class) return (T) Integer.valueOf(value.intValue());
        if (target == Long.class)    return (T) Long.valueOf(value.longValue());
        if (target == Float.class)   return (T) Float.valueOf(value.floatValue());
        if (target == Double.class)  return (T) Double.valueOf(value.doubleValue());

        throw new IllegalArgumentException("Unsupported number type: " + target);
    }

    public <T> T get(Class<T> clazz, Gamepad gamepad) {
        Object rawValue = valueProvider.apply(gamepad);

        if (rawValue instanceof Number && Number.class.isAssignableFrom(clazz)) {
            return convertNumber((Number) rawValue, clazz);
        }

        return clazz.cast(rawValue);
    }

    public float getFloatCubic(Gamepad gamepad) {
        return (float) Math.pow(get(Float.class, gamepad), 3);
    }
}