package team.phantompanthers.opcode.auto_op.impl;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import team.phantompanthers.opcode.auto_op.BaseAutoOpCode;
import team.phantompanthers.hardware.DriveTrain;
import team.phantompanthers.hardware.GyroSensor;


@Autonomous(name = "Normal Autonomous", group = "Linear Opmode")
public class AutoOpNormalFunction extends BaseAutoOpCode {
    private final DriveTrain drive = new DriveTrain();
    private final GyroSensor gyro = new GyroSensor();

    @Override
    public void runOpMode() {
        drive.init(hardwareMap);
        gyro.init(hardwareMap);

        // Show initial direction.
        while (opModeInInit()) {
            telemetry.addData("Heading (deg)", "%.1f", gyro.getHeadingDegrees());
            telemetry.update();
        }

        waitForStart();
        if (isStopRequested()) return;

        // set yaw to zero when starting.
        gyro.resetYaw();
        telemetry.addLine("Yaw reset.");
        telemetry.update();

        /* Simple Test
        * 1. Turn 90 Degrees
        * 2. Go back to Normal Orientation
        * 3. Turn -90 Degrees
        * 4. Hold -90 Heading precisely.
        */
        turnToHeading(90, 0.03, 0.5, 4000);
        sleep(300);
        turnToHeading(0, 0.03, 0.5, 4000);
        sleep(300);
        turnToHeading(-90,0.03, 0.5, 4000);
        sleep(300);

        long holdEnd = System.currentTimeMillis() + 1000;
        while (opModeIsActive() && System.currentTimeMillis() < holdEnd) {
            double rot = gyro.steeringCorrection(-90, 0.02);
            rot = Range.clip(rot, -0.3, 0.3);
            drive.drive(0, 0, rot);
            telemetry.addData("Hold Heading", -90);
            telemetry.addData("Current Heading", "%.1f", gyro.getHeadingDegrees());
            telemetry.addData("Turn Power", "%.2f", rot);
            telemetry.update();
            sleep(20);
        }

        // Finish test
        drive.drive(0, 0, 0);
        telemetry.addLine("Gyro Done");
        telemetry.update();
        sleep(250);
    }

    private void turnToHeading(double targetDeg, double kP, double maxPower, long timeoutMs) {
        final double TOLERANCE = 2.0;
        long end = System.currentTimeMillis() + timeoutMs;

        while (opModeIsActive() && System.currentTimeMillis() < end) {
            double correction = gyro.steeringCorrection(targetDeg, kP);
            correction = Range.clip(correction, -maxPower, maxPower);

            drive.drive(0, 0, correction);

            double heading = gyro.getHeadingDegrees();
            double error = wrapAngle(targetDeg - heading);
            telemetry.addData("Target", "%.1f°", targetDeg);
            telemetry.addData("Heading", "%.1f°", heading);
            telemetry.addData("Error", "%.1f°", error);
            telemetry.addData("Turn Power", "%.2f", correction);
            telemetry.update();

            if (Math.abs(error) <= TOLERANCE) break;
            sleep(20);
        }

        drive.drive(0, 0, 0);
    }

    private static double wrapAngle(double deg) {
        while (deg > 180) deg -= 360;
        while (deg <= -180) deg += 360;
        return deg;
    }
}
