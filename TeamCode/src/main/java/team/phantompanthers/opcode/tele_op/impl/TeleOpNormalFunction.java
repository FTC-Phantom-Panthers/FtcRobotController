package team.phantompanthers.opcode.tele_op.impl;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import team.phantompanthers.hardware.PowerScaler;
import team.phantompanthers.opcode.tele_op.BaseTeleOpCode;
import team.phantompanthers.hardware.DriveTrain;
import team.phantompanthers.ControlMappings;

@TeleOp(name = "Normal Function", group = "Linear Opmode")
public class TeleOpNormalFunction extends BaseTeleOpCode {
    private static final int BALL_FIND_TIMEOUT_MS = 5000;
    private static final int KICK_STAY_TIME_MS = 1000;

    private static final int SPIN_STAY_TIME_MS = 50;

    private final DriveTrain drive = new DriveTrain();
    private boolean lastKickValue = false;
    private boolean kickToggle = false;
    private long kickStartTime = -1;
    private long findColorStartTime = -1;

    private BallToFind ballToFind = BallToFind.NONE;

    private long foundBallTime = -1;

    @Override
    public void runOpMode() {
        drive.init(hardwareMap);
        PowerScaler powerScaler = new PowerScaler(hardwareMap.voltageSensor.iterator().next());

        //initVisionProcessors();
        waitForStart();
        while (opModeIsActive()) {
            double x = ControlMappings.MOVEMENT_X.getFloatCubic(gamepad1);
            double y = -ControlMappings.MOVEMENT_Y.getFloatCubic(gamepad1);
            double rot = ControlMappings.ROTATION.get(Float.class, gamepad1);

            drive.drive(x, y, rot);

            drive.intake(ControlMappings.INTAKE.get(Float.class, gamepad1));
            drive.wheelSpin(ControlMappings.WHEEL_SPIN.get(Float.class, gamepad1));
            drive.launcherSpin(ControlMappings.LAUNCHER_SPIN.get(Float.class, gamepad1));

            if (ControlMappings.LAUNCH.get(Boolean.class, gamepad1)) {
                kickToggle = !kickToggle;
                kickStartTime = System.currentTimeMillis();
            }

            if (kickStartTime > -1 && System.currentTimeMillis() - kickStartTime > KICK_STAY_TIME_MS) {
                kickToggle = false;
                kickStartTime = -1;
            }

            drive.launcherKick(kickToggle ? 0.4f : 0.7f);

            if (ControlMappings.SPIN_TO_PURPLE.get(Boolean.class, gamepad1)) {
                ballToFind = BallToFind.PURPLE;
                findColorStartTime = System.currentTimeMillis();
                foundBallTime = -1;
            } else if (ControlMappings.SPIN_TO_GREEN.get(Boolean.class, gamepad1)) {
                ballToFind = BallToFind.GREEN;
                findColorStartTime = System.currentTimeMillis();
                foundBallTime = -1;
            }

            if (ControlMappings.FIND_CANCEL.get(Boolean.class, gamepad1)) {
                ballToFind = BallToFind.NONE;
                findColorStartTime = -1;
                foundBallTime = -1;
            }

            if (ballToFind != BallToFind.NONE && System.currentTimeMillis() - findColorStartTime > BALL_FIND_TIMEOUT_MS) {
                ballToFind = BallToFind.NONE;
                findColorStartTime = -1;
                foundBallTime = -1;
                telemetry.addLine("no " + ballToFind.name().toLowerCase() + " ball found");
            }

            if (ballToFind != BallToFind.NONE) {
                float[] hsv = new float[3];
                Color.colorToHSV(drive.colorSensor.argb(), hsv);

                if (ballToFind.isHueCorrect(hsv[0])) {
                    ballToFind = BallToFind.NONE;
                    findColorStartTime = -1;
                    drive.wheelSpin(-0.4f);
                    drive.intake(0);
                    foundBallTime = System.currentTimeMillis();
                } else {
                    drive.wheelSpin(0.4f);
                    drive.intake(1);
                }
            }

            if (System.currentTimeMillis() - foundBallTime < SPIN_STAY_TIME_MS) {
                drive.wheelSpin(-0.4f);
                drive.intake(0);
            }

            hsvTelemetry();

            telemetry.update();

            sleep(20);
        }

        //visionPortal.close();
    }

    private void hsvTelemetry() {
        float[] hsv = new float[3];
        Color.colorToHSV(drive.colorSensor.argb(), hsv);
        telemetry.addData("Hue", hsv[0]);
        telemetry.addData("Saturation", hsv[1]);
        telemetry.addData("Brightness", hsv[2]);
    }
}
