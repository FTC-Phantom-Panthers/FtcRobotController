package team.phantompanthers.hardware;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class DriveTrain {
    public DcMotorEx lf, rf, lb, rb;

    public Servo launcherAngleX;
    public Servo launcherAngleY;
    public DcMotor launcherCharger;

    public void init(HardwareMap hw) {
        lf = hw.get(DcMotorEx.class, "frontLeftMotor");
        rf = hw.get(DcMotorEx.class, "frontRightMotor");
        lb = hw.get(DcMotorEx.class, "backLeftMotor");
        rb = hw.get(DcMotorEx.class, "backRightMotor");

        launcherAngleX = hw.get(Servo.class, "launcherAngleX");

        launcherAngleY = hw.get(Servo.class, "launcherAngleY");
        launcherCharger = hw.get(DcMotor.class, "launcherCharger");

        // Initial directions so +Y and +X correlate correctly
        lf.setDirection(DcMotor.Direction.REVERSE);
        lb.setDirection(DcMotor.Direction.REVERSE);
        rf.setDirection(DcMotor.Direction.FORWARD);
        rb.setDirection(DcMotor.Direction.FORWARD);

        launcherCharger.setDirection(DcMotorSimple.Direction.FORWARD);
        launcherCharger.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        launcherCharger.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Brake when no input, run open-loop in TeleOp
        for (DcMotorEx m : new DcMotorEx[]{lf, rf, lb, rb}) {
            m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            m.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    public void setLauncherPower(double power) {
        launcherCharger.setPower(power);
    }

    public void rotateLauncherToAngle(double angleX, double angleY) {
        launcherAngleX.setPosition(0.75f + angleX);
        launcherAngleY.setPosition(angleY);
    }

    public void drive(double x, double y, double rot) {
        // Usual Mecanum mixing
        double lfP = y + x + rot;
        double rfP = y - x - rot;
        double lbP = y - x + rot;
        double rbP = y + x - rot;

        // Normalize it so no wheel can be greater than 1
        double max = Math.max(1.0, Math.max(Math.abs(lfP), Math.max(Math.abs(rfP), Math.max(Math.abs(lbP), Math.abs(rbP)))));
        lf.setPower(lfP / max);
        rf.setPower(rfP / max);
        lb.setPower(lbP / max);
        rb.setPower(rbP / max);
    }
}
