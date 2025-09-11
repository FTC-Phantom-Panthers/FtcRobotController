package team.phantompanthers.opcode.tele_op.impl;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import team.phantompanthers.opcode.tele_op.BaseTeleOpCode;

@TeleOp(name = "Normal Function", group = "Linear Opmode")
public class TeleOpNormalFunction extends BaseTeleOpCode {
    @Override
    public void runOpMode() {
        initAprilTag();

        waitForStart();
        while (opModeIsActive()) {
            telemetryAprilTag();

            telemetry.update();

            // im not sure why we have to yield the cpu, ftc example said to do this
            sleep(20);
        }

        visionPortal.close();
    }
}
