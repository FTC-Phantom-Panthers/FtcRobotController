package team.phantompanthers.localization;

import java.util.HashMap;
import java.util.Map;

// Didn't put in official field coordinates yet :p
public class DecodeTagMap {
    public static class TagPose2d {
        public final double x, y, headingDeg;
        public TagPose2d(double x, double y, double headingDeg) {
            this.x = x;
            this.y = y;
            this.headingDeg = headingDeg;
        }
    }

    public static final Map<Integer, TagPose2d> TAGS = new HashMap<>();
    static {
        // Blue Goal (ID = 20)
        TAGS.put(20, new TagPose2d(60.0, 60.0, 180.0));
        // Red Goal (ID = 24)
        TAGS.put(24, new TagPose2d(-60.0, -60.0, 0.0));
    }
}
