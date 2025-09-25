package team.phantompanthers.localization;

import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import java.util.List;
import static java.lang.Math.*;

public class AprilTagIMULocalizer {
    private final Pose2d pose = new Pose2d();
    private final double camRightIn, camForwardIn, camYawDeg;
    private final double maxTrustRangeIn;

    public AprilTagIMULocalizer(double camRightIn, double camForwardIn, double camYawDeg, double maxTrustRangeIn) {
        this.camRightIn = camRightIn;
        this.camForwardIn = camForwardIn;
        this.camYawDeg = camYawDeg;
        this.maxTrustRangeIn = maxTrustRangeIn;
    }

    public void update(double imuHeadingDeg, List<AprilTagDetection> detections) {
        pose.headingDeg = normalize(imuHeadingDeg);

        AprilTagDetection best = null;
        for (AprilTagDetection d : detections) {
            if (d.metadata == null) continue;    // unknown tags are ignored
            int id = d.id;
            if (id != 20 && id != 24) continue; // obelisk tags are ignored
            if (d.ftcPose == null) continue;
            if (d.ftcPose.range > maxTrustRangeIn) continue;
            if (best == null || d.ftcPose.range < best.ftcPose.range) best = d;
        }
        if (best == null) return;

        DecodeTagMap.TagPose2d tagW = DecodeTagMap.TAGS.get(best.id);
        if (tagW == null) return; // no map

        double vCamX = best.ftcPose.x;
        double vCamY = best.ftcPose.y;

        double camYawRad = toRadians(normalize(pose.headingDeg + camYawDeg));
        double cosH = cos(camYawRad), sinH = sin(camYawRad);
        double vFieldX = vCamX * cosH - vCamY * sinH;
        double vFieldY = vCamX * sinH + vCamY * cosH;

        double camFieldX = tagW.x - vFieldX;
        double camFieldY = tagW.y - vFieldY;

        double rFieldX = camFieldX - (camRightIn * cosH - camForwardIn * sinH);
        double rFieldY = camFieldY - (camRightIn * sinH + camForwardIn * cosH);

        double range = max(1.0, best.ftcPose.range);
        double alpha = clamp(0.8 * (maxTrustRangeIn / (range + maxTrustRangeIn)), 0.15, 0.85);

        pose.x = lerp(pose.x, rFieldX, alpha);
        pose.y = lerp(pose.y, rFieldY, alpha);
    }

    private static double lerp(double a, double b, double t) { return a + (b - a) * t; }
    private static double clamp(double v, double lo, double hi) { return v < lo ? lo : (v > hi ? hi : v); }
    private static double normalize(double deg) {
        double d = deg;
        while (d > 180) d -= 360;
        while (d <= -180) d += 360;
        return d;
    }
}
