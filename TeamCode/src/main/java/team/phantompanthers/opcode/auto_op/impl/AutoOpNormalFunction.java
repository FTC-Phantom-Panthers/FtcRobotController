package team.phantompanthers.opcode.auto_op.impl;

import com.acmerobotics.roadrunner.Pose2d;
import com.goacmerobotics.roadrunner.utils.MecanumDrive;
import com.goacmerobotics.roadrunner.utils.tuning.LocalizationTest;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import team.phantompanthers.hardware.DriveTrain;
import team.phantompanthers.opcode.auto_op.BaseAutoOpCode;


@Autonomous(name = "Normal Autonomous", group = "Linear Opmode")
public class AutoOpNormalFunction extends BaseAutoOpCode {
    private final DriveTrain drive = new DriveTrain();

    @Override
    public void runOpMode() {
        drive.init(hardwareMap);

        waitForStart();

        drive.drive(0, 1, 0);
        sleep(500L);
    }
}
