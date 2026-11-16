package team.phantompanthers.opcode.tele_op.impl;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;

import team.phantompanthers.hardware.PowerScaler;
import team.phantompanthers.opcode.tele_op.BaseTeleOpCode;
import team.phantompanthers.hardware.DriveTrain;
import team.phantompanthers.ControlMappings;

@TeleOp(name = "Normal Function", group = "Linear Opmode")
public class TeleOpNormalFunction extends BaseTeleOpCode {
    private final DriveTrain drive = new DriveTrain();

    private static final RevHubOrientationOnRobot.LogoFacingDirection IMU_LOGO_DIRECTION =
            RevHubOrientationOnRobot.LogoFacingDirection.UP;
    private static final RevHubOrientationOnRobot.UsbFacingDirection IMU_USB_DIRECTION =
            RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;
    private IMU imu;

    private long lastLoopTime = 0;

    private double angleYCurrent = 1f;
    private float flowerCraneAngle = 0f;

    @Override
    public void runOpMode() {
        drive.init(hardwareMap);
        PowerScaler powerScaler = new PowerScaler(hardwareMap.voltageSensor.iterator().next());

        imu = hardwareMap.get(IMU.class, "imu");
        if (!imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(
                IMU_LOGO_DIRECTION, IMU_USB_DIRECTION)))) {
            throw new IllegalStateException("Odometry IMU initialization failed");
        }

        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double x = ControlMappings.MOVEMENT_X.getFloatCubic(gamepad1);
            double y = ControlMappings.MOVEMENT_Y.getFloatCubic(gamepad1);
            double rot = ControlMappings.ROTATION.getFloatCubic(gamepad1);

            drive.drive(x, y, rot);

            drive.setLauncherPower(ControlMappings.LAUNCHER_CHARGE.get(Float.class, gamepad2));
            drive.setBallElevatorPower(ControlMappings.LAUNCHER_ELEVATOR.get(Float.class, gamepad2));
            drive.setIntakePower(ControlMappings.INTAKE.get(Float.class, gamepad2));


            flowerCraneAngle += ControlMappings.FLOWER_CRANE.get(Float.class, gamepad2) / 100f;
            flowerCraneAngle = Math.min(Math.max(flowerCraneAngle, 0f), 1f);

//            drive.setFlowerCraneAngle(flowerCraneAngle);
//            telemetry.addData("Flower Crane Current", flowerCraneAngle);

            angleYCurrent += ControlMappings.LAUNCHER_ANGLE_Y.get(Float.class, gamepad2) / 100f;
            angleYCurrent = Math.min(Math.max(0.8f, angleYCurrent), 1);

            double angle = drive.rotateLauncherToAngle(
                    ControlMappings.LAUNCHER_ANGLE_X.get(Float.class, gamepad2),
                    angleYCurrent,
                    (System.nanoTime() - lastLoopTime) / 1_000_000.0
            );

            telemetry.addData("LAUNCHER ANGLE X", ControlMappings.LAUNCHER_ANGLE_X.get(Float.class, gamepad1));
            telemetry.addData("LAUNCHER ANGLE X CURRENT", angle);
            telemetry.addData("LAUNCHER ANGLE Y CURRENT", angleYCurrent);
            telemetry.update();

            lastLoopTime = System.nanoTime();

            sleep(20);
        }

    }
}
