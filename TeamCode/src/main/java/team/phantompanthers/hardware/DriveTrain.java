package team.phantompanthers.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class DriveTrain {
    public DcMotorEx lf, rf, lb, rb, intake_sys, launcher_kick, launcher_wheel;

    public void init(HardwareMap hw) {
        lf = hw.get(DcMotorEx.class, "topLeft");
        rf = hw.get(DcMotorEx.class, "topRight");
        lb = hw.get(DcMotorEx.class, "backLeft");
        rb = hw.get(DcMotorEx.class, "backRight");
        launcher_kick = hw.get(DcMotorEx.class, "launcher_kick");
        launcher_wheel = hw.get(DcMotorEx.class,"launcher_wheel");

        intake_sys = hw.get(DcMotorEx.class, "intakeSys");

        // Initial directions so +Y and +X correlate correctly
        lf.setDirection(DcMotorSimple.Direction.REVERSE);
        lb.setDirection(DcMotor.Direction.REVERSE);
        rf.setDirection(DcMotor.Direction.FORWARD);
        rb.setDirection(DcMotor.Direction.FORWARD);

        // Brake when no input, run open-loop in TeleOp
        for (DcMotorEx m : new DcMotorEx[]{lf, rf, lb, rb}) {
            m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            m.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    public void intake(boolean intake_button, double power){
        if(intake_button){
            intake_sys.setPower(power);
        } else{
            intake_sys.setPower(0);
        }
    }

    public void launcher_kick(boolean launch_button, double power){
        if(launch_button){
            launcher_kick.setPower(power);
        } else{
            launcher_kick.setPower(0);
        }
    }
    public void launcher_spin(boolean spin_button, double power){
        if(spin_button){
            launcher_wheel.setPower(power);
        } else{
            launcher_wheel.setPower(0);
        }
    }

    public void drive(double x, double y, double rot) {
        // Usual Mecanum mixing
        double lfP = y +x + rot;
        double rfP = y - x - rot;
        double lbP = y - x + rot;
        double rbP = y + x - rot;

        // Normalize it so no wheel can be greater than 1
        double max = Math.max(1.0, Math.max(Math.abs(lfP), Math.max(Math.abs(rfP), Math.max(Math.abs(lbP), Math.abs(rbP)))));
        lf.setPower(-(lfP / max));
        rf.setPower(rfP / max);
        lb.setPower(lbP / max);
        rb.setPower(rbP / max);
    }
}
