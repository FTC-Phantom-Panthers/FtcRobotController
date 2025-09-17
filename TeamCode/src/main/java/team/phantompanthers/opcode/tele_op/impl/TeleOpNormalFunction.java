package team.phantompanthers.opcode.tele_op.impl;

import android.service.controls.Control;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import team.phantompanthers.opcode.tele_op.BaseTeleOpCode;
import team.phantompanthers.hardware.DriveTrain;
import team.phantompanthers.ControlMappings;

@TeleOp(name = "Normal Function", group = "Linear Opmode")
public class TeleOpNormalFunction extends BaseTeleOpCode {
    private final DriveTrain drive = new DriveTrain();

    @Override
    public void runOpMode() {
        // Initializations
        initAprilTag();
        drive.init(hardwareMap);

        waitForStart();
        while (opModeIsActive()) {
            // Go to ControlMapping.java for Controls (Cubic for easier control)
            double x = ControlMappings.MOVEMENT_X.getFloatCubic(gamepad1);
            double y = -ControlMappings.MOVEMENT_Y.getFloatCubic(gamepad1);
            double rot = (double) ControlMappings.TURN_RIGHT.getFloatCubic(gamepad1) - (double) ControlMappings.TURN_LEFT.getFloatCubic(gamepad1);

            drive.drive(x, y, rot);

            telemetryAprilTag();
            telemetry.update();

            // Paces loop to 50 Hz so LinearOpMode yields to the CPU and avoids stuttering
            sleep(20);
        }

        visionPortal.close();
    }
}
