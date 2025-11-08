package team.phantompanthers.hardware;

import com.qualcomm.robotcore.hardware.VoltageSensor;

public class PowerScaler {
    private static final float MAX_VOLTAGE = 12.9f;

    private final VoltageSensor voltageSensor;
    public PowerScaler(VoltageSensor voltageSensor) {
        this.voltageSensor =voltageSensor;
    }

    public double scalePower(double power) {
        return power * (voltageSensor.getVoltage() / MAX_VOLTAGE);
    }
}
