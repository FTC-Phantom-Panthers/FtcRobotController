package team.phantompanthers.opcode.tele_op.impl;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import team.phantompanthers.hardware.PowerScaler;
import team.phantompanthers.opcode.tele_op.BaseTeleOpCode;
import team.phantompanthers.hardware.DriveTrain;
import team.phantompanthers.ControlMappings;

@TeleOp(name = "Normal Function", group = "Linear Opmode")
public class TeleOpNormalFunction extends BaseTeleOpCode {
    private final DriveTrain drive = new DriveTrain();

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

            drive.drive(x, y, rot);

            telemetry.update();

            sleep(20);
        }

        visionPortal.close();
    }
}
