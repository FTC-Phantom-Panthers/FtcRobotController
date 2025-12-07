package team.phantompanthers.opcode.auto_op.impl;

import com.acmerobotics.roadrunner.Pose2d;
import com.goacmerobotics.roadrunner.utils.MecanumDrive;
import com.goacmerobotics.roadrunner.utils.tuning.LocalizationTest;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import team.phantompanthers.opcode.auto_op.BaseAutoOpCode;


@Autonomous(name = "Normal Autonomous", group = "Linear Opmode")
public class AutoOpNormalFunction extends BaseAutoOpCode {
    private MecanumDrive drive;

    @Override
    public void runOpMode() {
        drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));

        waitForStart();
    }
}
