package team.phantompanthers.opcode;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.opencv.ImageRegion;
import org.firstinspires.ftc.vision.opencv.PredominantColorProcessor;

import java.util.List;
import java.util.Locale;

public abstract class BaseOpCode extends LinearOpMode {
    protected static final Size WEBCAM_RESOLUTION = new Size(640, 480);

    protected AprilTagProcessor aprilTag;
    protected VisionPortal visionPortal;
    protected PredominantColorProcessor colorSensor;

    protected void initColorSensor() {

    }

    protected void initVisionProcessors() {
        colorSensor = new PredominantColorProcessor.Builder()
                .setRoi(ImageRegion.asUnityCenterCoordinates(-0.1, 0.1, 0.1, -0.1))
                .setSwatches(
                        PredominantColorProcessor.Swatch.ARTIFACT_GREEN,
                        PredominantColorProcessor.Swatch.ARTIFACT_PURPLE,
                        PredominantColorProcessor.Swatch.RED,
                        PredominantColorProcessor.Swatch.BLUE,
                        PredominantColorProcessor.Swatch.YELLOW,
                        PredominantColorProcessor.Swatch.BLACK,
                        PredominantColorProcessor.Swatch.WHITE)
                .build();

        aprilTag = new AprilTagProcessor.Builder()
                .setDrawAxes(false)
                .setDrawCubeProjection(false)
                .setDrawTagOutline(true)
                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                .setTagLibrary(getDecodeTagLibrary())
                .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)
                .build();

        aprilTag.setDecimation(3);

        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(WEBCAM_RESOLUTION)
                .enableLiveView(true)
                .setStreamFormat(VisionPortal.StreamFormat.YUY2)
                .setAutoStopLiveView(false)
                .addProcessor(aprilTag)
                .addProcessor(colorSensor)
                .build();

        visionPortal.setProcessorEnabled(aprilTag, true);
    }

    // Set up the AprilTag system to use DECODE defaults
    protected AprilTagLibrary getDecodeTagLibrary() {
        AprilTagLibrary.Builder b = new AprilTagLibrary.Builder();

        b.addTag(new AprilTagMetadata(20, "BLUE_GOAL", 8.125, DistanceUnit.INCH));
        b.addTag(new AprilTagMetadata(24, "RED_GOAL",  8.125, DistanceUnit.INCH));

        return b.build();
    }

    protected void debugAprilTag() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections.size());

        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                telemetry.addLine(String.format(Locale.getDefault(), "\n==== (ID %d) %s", detection.id, detection.metadata.name));
                telemetry.addLine(String.format(Locale.getDefault(), "XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                telemetry.addLine(String.format(Locale.getDefault(), "PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                telemetry.addLine(String.format(Locale.getDefault(), "RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
            } else {
                telemetry.addLine(String.format(Locale.getDefault(), "\n==== (ID %d) Unknown", detection.id));
                telemetry.addLine(String.format(Locale.getDefault(), "Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
            }

            // distance calculation
            {
                double x1 = detection.corners[0].x;
                double x2 = detection.corners[1].x;
                double x3 = detection.corners[2].x;
                double x4 = detection.corners[3].x;
                double y1 = detection.corners[0].y;
                double y2 = detection.corners[1].y;
                double y3 = detection.corners[2].y;
                double y4 = detection.corners[3].y;

                double area = Math.abs((x1 * y2 + x2 * y3 + x3 * y4 + x4 * y1)
                        - (y1 * x2 + y2 * x3 + y3 * x4 + y4 * x1)) / 2.0;

                int viewArea = WEBCAM_RESOLUTION.getWidth() * WEBCAM_RESOLUTION.getHeight();

                // what percent of the view the april tag is taking
                double viewPercentage = area / viewArea;

                telemetry.addLine("View area percent: " + String.format(Locale.getDefault(), "%.2f", viewPercentage) + "%");
            }
        }

        // Add "key" information to telemetry
        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
        telemetry.addLine("RBE = Range, Bearing & Elevation");
    }

    protected void debugColorSensor() {
        PredominantColorProcessor.Result result = colorSensor.getAnalysis();

        telemetry.addLine(String.format(Locale.getDefault(), "RGB   (%3d, %3d, %3d)",
                result.RGB[0], result.RGB[1], result.RGB[2]));
        telemetry.addLine("Closest color swatch: " + result.closestSwatch.name());
    }
}
