package team.phantompanthers.hardware;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Odo {
    public DcMotorEx lf, rf, lb, rb;
    private int prevULeft, prevURight, prevDLeft, prevDRight;
    private int currentULeft, currentURight, currentDLeft, currentDRight;

    public double x_cord = 0.0;
    public double y_cord = 0.0;
    public double heading = 0.0;

    //TODO: IDK add measurements?? (NOTES THESE ARE FILLER MEASUREMENTS SO THE CODE DONT BOOM WE NEED TO RECHECK THESE!!!)

    private static final double TICKS_PER_REV = 8192.0;
    private static final double WHEEL_RADIUS = 0.69;
    private static final double GEAR_RATIO = 1.0;

    private static final double TRACK_WIDTH = 12.0;
    private static final double PERP_OFFSET = -3.0;

    private static final double INCHES_PER_TICK =
            (2.0 * Math.PI * WHEEL_RADIUS * GEAR_RATIO)
                    / TICKS_PER_REV;

    public void InitOdo(HardwareMap hw){
        lf = hw.get(DcMotorEx.class, "frontLeftMotor");
        rf = hw.get(DcMotorEx.class, "frontRightMotor");
        lb = hw.get(DcMotorEx.class, "backLeftMotor");
        rb = hw.get(DcMotorEx.class, "backRightMotor");


    }

    double gimme_inches(int current, int last){
        return (current - last) * INCHES_PER_TICK;
    }

    public void reset(){
        //NOTE: We probably got reconfigure if the encoder is on only one wheel.

        prevULeft = lf.getCurrentPosition();
        prevDLeft = lb.getCurrentPosition();
        prevURight = rf.getCurrentPosition();
        prevDRight = rb.getCurrentPosition();

        x_cord = 0.0;
        y_cord = 0.0;
        heading = 0.0;
    }

    public void update(){
        currentULeft = lf.getCurrentPosition();
        currentDLeft = lb.getCurrentPosition();
        currentURight = rf.getCurrentPosition();
        currentDRight = rb.getCurrentPosition();

        double dULeft = gimme_inches(currentULeft, prevULeft);
        double dBLeft = gimme_inches(currentDLeft, prevDLeft);
        double dURight = gimme_inches(currentURight, prevURight);
        double dBRight = gimme_inches(currentDRight, prevDRight);

       prevDLeft = currentDLeft;
       prevDRight = currentDRight;
       prevURight = currentURight;
       prevULeft = currentULeft;


       //TODO: Calculations and stuff
    }
}
