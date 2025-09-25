package team.phantompanthers.localization;

public class Pose2d {
    public double x, y, headingDeg;

    public Pose2d() { this(0,0,0); }

    public Pose2d(double x, double y, double headingDeg) {
        this.x = x;
        this.y = y;
        this.headingDeg = headingDeg;
    }
}
