package team.phantompanthers.opcode;

import android.graphics.Color;
import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.opencv.ImageRegion;
import org.firstinspires.ftc.vision.opencv.PredominantColorProcessor;
import org.opencv.core.Point;

import java.util.List;
import java.util.Locale;

public abstract class BaseOpCode extends LinearOpMode {
    protected static final Size WEBCAM_RESOLUTION = new Size(640, 480);

    protected AprilTagProcessor aprilTag;
    protected VisionPortal visionPortal;
    protected PredominantColorProcessor colorSensor;

    protected void initColorSensor() {
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
    }

    protected void telemetryColorSensor() {
        PredominantColorProcessor.Result result = colorSensor.getAnalysis();

        telemetry.addLine(String.format("RGB   (%3d, %3d, %3d)",
                result.RGB[0], result.RGB[1], result.RGB[2]));
        telemetry.addLine("Closest color swatch: " + result.closestSwatch.name());
    }

    /**
     * Initialize the AprilTag processor.
     */
    protected void initAprilTag(boolean addColorSensor) {
        // Create the AprilTag processor.
        aprilTag = new AprilTagProcessor.Builder()

                // The following default settings are available to un-comment and edit as needed.
                .setDrawAxes(false)
                .setDrawCubeProjection(false)
                .setDrawTagOutline(true)
                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                .setTagLibrary(getDecodeTagLibrary())
                .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)


                // == CAMERA CALIBRATION ==
                // If you do not manually specify calibration parameters, the SDK will attempt
                // to load a predefined calibration for your camera.
                //.setLensIntrinsics(578.272, 578.272, 402.145, 221.506)
                // ... these parameters are fx, fy, cx, cy.

                .build();

        // Adjust Image Decimation to trade-off detection-range for detection-rate.
        // eg: Some typical detection data using a Logitech C920 WebCam
        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second (default)
        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second (default)
        // Note: Decimation can be changed on-the-fly to adapt during a match.
        aprilTag.setDecimation(3);

        // Create the vision portal by using a builder.
        VisionPortal.Builder builder = new VisionPortal.Builder();

        // Set the camera (webcam vs. built-in RC phone camera).
        builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"));

        // Choose a camera resolution. Not all cameras support all resolutions.
        builder.setCameraResolution(WEBCAM_RESOLUTION);

        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
        builder.enableLiveView(true);

        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
        builder.setStreamFormat(VisionPortal.StreamFormat.YUY2);

        // Choose whether or not LiveView stops if no processors are enabled.
        // If set "true", monitor shows solid orange screen if no processors enabled.
        // If set "false", monitor shows camera view without annotations.
        builder.setAutoStopLiveView(false);

        // Set and enable the processor.
        builder.addProcessor(aprilTag);

        if (addColorSensor)
            builder.addProcessor(colorSensor);

        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();

        // Disable or re-enable the aprilTag processor at any time.
        visionPortal.setProcessorEnabled(aprilTag, true);
    }

    // Set up the AprilTag system to use DECODE defaults
    protected AprilTagLibrary getDecodeTagLibrary() {
        AprilTagLibrary.Builder b = new AprilTagLibrary.Builder();

        b.addTag(new AprilTagMetadata(20, "BLUE_GOAL", 8.125, DistanceUnit.INCH));
        b.addTag(new AprilTagMetadata(24, "RED_GOAL",  8.125, DistanceUnit.INCH));

        return b.build();
    }

    /**
     * Add telemetry about AprilTag detections.
     */
    protected void telemetryAprilTag() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections.size());

        // Step through the list of detections and display info for each one.
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

                double viewArea = WEBCAM_RESOLUTION.getWidth() * WEBCAM_RESOLUTION.getHeight();

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
}
