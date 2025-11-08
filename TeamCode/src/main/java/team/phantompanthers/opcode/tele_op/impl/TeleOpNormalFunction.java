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
        Double power;
        // PowerScaler powerScaler = new PowerScaler(hardwareMap.voltageSensor.iterator().next());

        waitForStart();
        while (opModeIsActive()) {
            double x = ControlMappings.MOVEMENT_X.getFloatCubic(gamepad1);
            double y = -ControlMappings.MOVEMENT_Y.getFloatCubic(gamepad1);
            double rot = ControlMappings.TURN_RIGHT.getFloatCubic(gamepad1) - ControlMappings.TURN_LEFT.getFloatCubic(gamepad1);


            if(ControlMappings.KICK.getBoolean(gamepad1)){
                power = 1.0;
                drive.launcher_kick(ControlMappings.KICK.getBoolean(gamepad1),power);
            } else if(ControlMappings.KICK_INVERT.getBoolean(gamepad1)){
                power = -1.0;
                drive.launcher_kick(ControlMappings.KICK_INVERT.getBoolean(gamepad1),power);
            } else{
                drive.launcher_kick(false,0);
            }
            if(ControlMappings.INTAKE.getBoolean(gamepad1)){
                power = 1.0;
                drive.intake(ControlMappings.INTAKE.getBoolean(gamepad1),power);
            } else if(ControlMappings.INTAKE_INVERT.getBoolean(gamepad1)){
                power = -1.0;
                drive.intake(ControlMappings.INTAKE_INVERT.getBoolean(gamepad1),power);
            } else {
                drive.intake(false, 0);
            }
            if(ControlMappings.SPIN.getBoolean(gamepad1)){
                power = 1.0;
                drive.launcher_spin(ControlMappings.SPIN.getBoolean(gamepad1),power);
            } else if(ControlMappings.SPIN_INVERT.getBoolean(gamepad1)){
                power = -1.0;
                drive.launcher_spin(ControlMappings.SPIN_INVERT.getBoolean(gamepad1),power);
            }
            drive.drive(x, y, rot);

            telemetry.update();

            // Paces loop to 50 Hz so LinearOpMode yields to the CPU and avoids stuttering
            sleep(20);
        }

        // visionPortal.close();
    }
}
