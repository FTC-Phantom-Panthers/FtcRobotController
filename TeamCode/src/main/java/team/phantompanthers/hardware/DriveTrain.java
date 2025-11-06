package team.phantompanthers.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class DriveTrain {
    public DcMotorEx lf, rf, lb, rb;

    public void init(HardwareMap hw) {
        lf = hw.get(DcMotorEx.class, "topLeft");
        rf = hw.get(DcMotorEx.class, "topRight");
        lb = hw.get(DcMotorEx.class, "backLeft");
        rb = hw.get(DcMotorEx.class, "backRight");

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

    public void drive(double x, double y) {
        double lfP = Math.min(1, Math.max(-1, -x + y));
        double rfP = -Math.min(1, Math.max(-1, -x - y));
        double lbP = -Math.min(1, Math.max(-1, -x - y));
        double rbP = Math.min(1, Math.max(-1, -x + y));

        lf.setPower(lfP);
        rf.setPower(rfP);
        lb.setPower(lbP);
        rb.setPower(rbP);
    }

    public void driveTurnLeft(double power) {
        if (power > 0) {
            lf.setPower(power);
            rf.setPower(-power);
            lb.setPower(power);
            rb.setPower(-power);
        }
    }

    public void driveTurnRight(double power) {
        if (power > 0) {
            lf.setPower(-power);
            rf.setPower(power);
            lb.setPower(-power);
            rb.setPower(power);
        }
    }
}
