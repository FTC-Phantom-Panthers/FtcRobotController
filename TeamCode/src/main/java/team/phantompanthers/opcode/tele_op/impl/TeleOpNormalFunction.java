package team.phantompanthers.opcode.tele_op.impl;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import team.phantompanthers.hardware.PowerScaler;
import team.phantompanthers.opcode.tele_op.BaseTeleOpCode;
import team.phantompanthers.hardware.DriveTrain;
import team.phantompanthers.ControlMappings;

@TeleOp(name = "Normal Function", group = "Linear Opmode")
public class TeleOpNormalFunction extends BaseTeleOpCode {
    private final DriveTrain drive = new DriveTrain();
    private boolean lastKickValue = false;
    private boolean kickToggle = false;
    private long kickStartTime = -1;
    private long findColorStartTime = -1;
    private boolean lastPurpleFind = false;
    private boolean lastGreenFind = false;
    private boolean lastFindCancel = false;
    private boolean findPurple = false;
    private boolean findGreen = false;


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

            drive.intake(ControlMappings.INTAKE.get(Float.class, gamepad1));
            drive.wheelSpin(ControlMappings.WHEEL_SPIN.get(Float.class, gamepad1));
            drive.launcherSpin(ControlMappings.LAUNCHER_SPIN.get(Float.class, gamepad1));

            if (ControlMappings.LAUNCH.get(Boolean.class, gamepad1) && !lastKickValue) {
                kickToggle = !kickToggle;
                kickStartTime = System.currentTimeMillis();
            }

            if (kickStartTime > -1 && System.currentTimeMillis() - kickStartTime > 1000) {
                kickToggle = false;
                kickStartTime = -1;
            }

            drive.launcherKick(kickToggle ? 0.4f : 0.7f);

            lastKickValue = ControlMappings.LAUNCH.get(Boolean.class, gamepad1);

            if (ControlMappings.SPIN_TO_PURPLE.get(Boolean.class, gamepad1) && !lastPurpleFind) {
                findPurple = true;
                findGreen = false;
                findColorStartTime = System.currentTimeMillis();
            } else if (ControlMappings.SPIN_TO_GREEN.get(Boolean.class, gamepad1) && !lastGreenFind) {
                findGreen = true;
                findPurple = false;
                findColorStartTime = System.currentTimeMillis();
            }

            if (ControlMappings.FIND_CANCEL.get(Boolean.class, gamepad1) && !lastFindCancel) {
                findPurple = false;
                findGreen = false;
                findColorStartTime = -1;
            }

            lastPurpleFind = ControlMappings.SPIN_TO_PURPLE.get(Boolean.class, gamepad1);
            lastGreenFind = ControlMappings.SPIN_TO_GREEN.get(Boolean.class, gamepad1);
            lastFindCancel = ControlMappings.FIND_CANCEL.get(Boolean.class, gamepad1);


            if (findPurple && findColorStartTime > -1 && System.currentTimeMillis() - findColorStartTime > 5000) {
                findPurple = false;
                findColorStartTime = -1;
                telemetry.addLine("no purple dumbass");
            }
            if (findGreen && findColorStartTime > -1 && System.currentTimeMillis() - findColorStartTime > 5000) {
                findGreen = false;
                findColorStartTime = -1;
                telemetry.addLine("no green dumbass");
            }

            if (findPurple) {
                float[] hsv = new float[3];
                Color.colorToHSV(drive.colorSensor.argb(), hsv);

                if (hsv[0] > 200) {
                    findPurple = false;
                    findColorStartTime = -1;
                    drive.wheelSpin(-0.4f);
                    drive.intake(0);
                    sleep(50);
                } else {
                    drive.wheelSpin(0.4f);
                    drive.intake(1);
                }
            }

            if (findGreen) {
                float[] hsv = new float[3];
                Color.colorToHSV(drive.colorSensor.argb(), hsv);

                if (hsv[0] > 100 && hsv[0] < 200) {
                    findGreen = false;
                    findColorStartTime = -1;
                    drive.wheelSpin(-0.4f);
                    drive.intake(0);
                    sleep(50);
                } else {
                    drive.wheelSpin(0.4f);
                    drive.intake(1);
                }
            }

            float[] hsv = new float[3];
            Color.colorToHSV(drive.colorSensor.argb(), hsv);
            telemetry.addData("Hue", hsv[0]);
            telemetry.addData("Saturation", hsv[1]);
            telemetry.addData("Brightness", hsv[2]);

            drive.drive(x, y, rot);
            telemetry.update();

            sleep(20);
        }

        //visionPortal.close();
    }
}
