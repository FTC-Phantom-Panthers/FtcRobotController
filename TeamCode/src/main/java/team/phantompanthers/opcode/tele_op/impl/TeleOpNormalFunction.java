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
        initColorSensor();
        initVisionProcessors();
        drive.init(hardwareMap);

        PowerScaler powerScaler = new PowerScaler(hardwareMap.voltageSensor.iterator().next());

        waitForStart();
        while (opModeIsActive()) {
            double x = ControlMappings.MOVEMENT_X.getFloatCubic(gamepad1);
            double y = -ControlMappings.MOVEMENT_Y.getFloatCubic(gamepad1);
            double rot = ControlMappings.TURN_RIGHT.getFloatCubic(gamepad1) - ControlMappings.TURN_LEFT.getFloatCubic(gamepad1);

            drive.drive(powerScaler.scalePower(x), powerScaler.scalePower(y), powerScaler.scalePower(rot));

            debugAprilTag();
            debugColorSensor();

            telemetry.update();

            sleep(20);
        }

        visionPortal.close();
    }
}
