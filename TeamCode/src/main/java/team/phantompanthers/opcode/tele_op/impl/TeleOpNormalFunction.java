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
    private static final int BALL_FIND_CYCLE_TIMEOUT_MS = 500;
    private static final int KICK_STAY_TIME_MS = 1000;

    private final DriveTrain drive = new DriveTrain();

    private int sorterPosition = 0;
    private boolean kickToggle = false;
    private long kickStartTime = -1;
    private long findColorStartTime = -1;

    private long findColorCycleTimeoutStartTime = -1;

    private BallToFind ballToFind = BallToFind.NONE;

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

            if (ControlMappings.SORTER_SPIN_CYCLE_UP.get(Boolean.class, gamepad1)) {
                sorterPosition++;
            }

            if (ControlMappings.SORTER_SPIN_CYCLE_DOWN.get(Boolean.class, gamepad1)) {
                sorterPosition--;
            }


            drive.wheelRotateTo(sorterPosition);

            drive.launcherSpin(ControlMappings.LAUNCHER_SPIN.get(Float.class, gamepad1));

            if (ControlMappings.LAUNCH.get(Boolean.class, gamepad1)) {
                kickToggle = !kickToggle;
                kickStartTime = System.currentTimeMillis();
            }

            if (kickStartTime > -1 && System.currentTimeMillis() - kickStartTime > KICK_STAY_TIME_MS) {
                kickToggle = false;
                kickStartTime = -1;
            }

            drive.launcherKick(kickToggle ? -1 : 0);

            telemetry.addData("Sorter Position", sorterPosition);
            telemetry.addData("Sorter Real Position", drive.getWheelPosition());

            if (ControlMappings.SPIN_TO_PURPLE.get(Boolean.class, gamepad1)) {
                ballToFind = BallToFind.PURPLE;
                findColorStartTime = System.currentTimeMillis();
            } else if (ControlMappings.SPIN_TO_GREEN.get(Boolean.class, gamepad1)) {
                ballToFind = BallToFind.GREEN;
                findColorStartTime = System.currentTimeMillis();
            }

            if (ControlMappings.FIND_CANCEL.get(Boolean.class, gamepad1)) {
                ballToFind = BallToFind.NONE;
                findColorStartTime = -1;
            }

            if (ballToFind != BallToFind.NONE && System.currentTimeMillis() - findColorStartTime > BALL_FIND_TIMEOUT_MS) {
                ballToFind = BallToFind.NONE;
                findColorStartTime = -1;
                telemetry.addLine("no " + ballToFind.name().toLowerCase() + " ball found");
            }

            if (ballToFind != BallToFind.NONE && !drive.isWheelBusy()) {
                if (findColorCycleTimeoutStartTime == -1)
                    findColorCycleTimeoutStartTime = System.currentTimeMillis();

                if (System.currentTimeMillis() - findColorCycleTimeoutStartTime > BALL_FIND_CYCLE_TIMEOUT_MS) {
                    findColorCycleTimeoutStartTime = -1;

                    float[] hsv = new float[3];
                    Color.colorToHSV(drive.colorSensor.argb(), hsv);

                    if (ballToFind.isHueCorrect(hsv[0])) {
                        ballToFind = BallToFind.NONE;
                        findColorStartTime = -1;
                    } else {
                        sorterPosition++;
                    }
                }
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
