package team.phantompanthers.hardware;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class GyroSensor {
    private IMU imu;
    private String deviceName = "imu";

    // Default Constructor
    public void init(HardwareMap hw) {
        init(hw, new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
    }

    public void init(HardwareMap hw, RevHubOrientationOnRobot orientation) {
        imu = hw.get(IMU.class, deviceName);
        imu.initialize(new IMU.Parameters(orientation));
    }

    public GyroSensor withDeviceName(String name) {
        this.deviceName = name;
        return this;
    }

    public double getHeadingDegrees() {
        return getHeading(AngleUnit.DEGREES);
    }

    public double getHeading(AngleUnit unit) {
        YawPitchRollAngles ypr = imu.getRobotYawPitchRollAngles();
        return ypr.getYaw(unit);
    }

    public YawPitchRollAngles getYawPitchRollAngles() {
        return imu.getRobotYawPitchRollAngles();
    }

    public void resetYaw() {
        imu.resetYaw();
    }

    /* HELPER FUNCTIONS */

    // Used for Drive Code
    public double steeringCorrection(double desiredHeadingDeg, double proportionalGain) {
        double error = desiredHeadingDeg - getHeadingDegrees();
        while (error > 180)  error -= 360;
        while (error <= -180) error += 360;
        return Range.clip(error * proportionalGain, -1, 1);
    }

    // For any Advanced IMU use.
    public IMU raw() {
        return imu;
    }
}
