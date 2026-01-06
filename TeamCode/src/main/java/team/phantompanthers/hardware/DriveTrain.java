package team.phantompanthers.hardware;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class DriveTrain {
    public DcMotorEx lf, rf, lb, rb, intake, wheel, launcher;
    public Servo launcherKick;
    public ColorSensor colorSensor;
    public void init(HardwareMap hw) {
        lf = hw.get(DcMotorEx.class, "topLeft");
        rf = hw.get(DcMotorEx.class, "topRight");
        lb = hw.get(DcMotorEx.class, "backLeft");
        rb = hw.get(DcMotorEx.class, "backRight");
        wheel = hw.get(DcMotorEx.class,"wheel");
        launcher = hw.get(DcMotorEx.class, "launcher");
        launcherKick = hw.get(Servo.class, "launcherKick");
        colorSensor = hw.get(ColorSensor.class, "colorSensor");

        intake = hw.get(DcMotorEx.class, "intake");

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

        //launcherKick.setPosition(-0.5);
    }

    public void intake(double power){
        intake.setPower(power);
    }
    public void wheelSpin(double power) {
        wheel.setPower(power);
    }

    public void launcherSpin(double power) {
        launcher.setPower(power);
    }

    public void launcherKick(double position) {
        launcherKick.setPosition(position);
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
