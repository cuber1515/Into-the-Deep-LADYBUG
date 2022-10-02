/*
 * Copyright (c) 2020 OpenFTC Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.firstinspires.ftc.masters;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


public class MultipleCameraCV {

//    Declare webcams
    public OpenCvWebcam duckWebcam;
    public OpenCvWebcam webcam;

//    Initial declaration of pipelines (One for each we use)
    public DuckDeterminationPipeline duckPipeline;
    public ShippingElementDeterminationPipeline pipeline;
    public WarehousePipeline warehousePipeline;

//    Random useful tidbit of information, to access the camera stream, hit innit, then
//    press the ellipsis button on the top right of the screen, then select "Camera Stream".
//    Follow same path to turn it off


    public MultipleCameraCV(HardwareMap hardwareMap, Telemetry telemetry){

//          Ignore this
//        int[] viewportContainerIds = OpenCvCameraFactory.getInstance()
//                .splitLayoutForMultipleViewports(
//                        cameraMonitorViewId, //The container we're splitting
//                        2, //The number of sub-containers to create
//                        OpenCvCameraFactory.ViewportSplitMethod.VERTICALLY); //Whether to split the container vertically or horizontally


//          This too
//        int[] viewportContainerIds = OpenCvCameraFactory.getInstance()
//                .splitLayoutForMultipleViewports(
//                        cameraMonitorViewId, //The container we're splitting
//                        2, //The number of sub-containers to create
//                        OpenCvCameraFactory.ViewportSplitMethod.VERTICALLY); //Whether to split the container vertically or horizontally


//        Get and store camera monitor view id.
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());


//        Assign both webcams and pipelines properly
        duckWebcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "WebcamDuck"),cameraMonitorViewId);
        duckPipeline = new DuckDeterminationPipeline(telemetry);


        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam"));
        pipeline = new ShippingElementDeterminationPipeline(telemetry);

        //warehousePipeline = new WarehousePipeline(telemetry);

        // We set the viewport policy to optimized view so the preview doesn't appear 90 deg
        // out when the RC activity is in portrait. We do our actual image processing assuming
        // landscape orientation, though.
        // webcam.setViewportRenderingPolicy(OpenCvCamera.ViewportRenderingPolicy.OPTIMIZE_VIEW);

        duckWebcam.setMillisecondsPermissionTimeout(2500);
        webcam.setMillisecondsPermissionTimeout(2500);

        // Open both camera streams. Closing unnecessary streams mid autonomous is possible and likely recommended.
        duckWebcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                /*
                 * Tell the webcam to start streaming images to us! Note that you must make sure
                 * the resolution you specify is supported by the camera. If it is not, an exception
                 * will be thrown.
                 *
                 * Keep in mind that the SDK's UVC driver (what OpenCvWebcam uses under the hood) only
                 * supports streaming from the webcam in the uncompressed YUV image format. This means
                 * that the maximum resolution you can stream at and still get up to 30FPS is 480p (640x480).
                 * Streaming at e.g. 720p will limit you to up to 10FPS and so on and so forth.
                 *
                 * Also, we specify the rotation that the webcam is used in. This is so that the image
                 * from the camera sensor can be rotated such that it is always displayed with the image upright.
                 * For a front facing camera, rotation is defined assuming the user is looking at the screen.
                 * For a rear facing camera or a webcam, rotation is defined assuming the camera is facing
                 * away from the user.
                 */
                telemetry.addData("Duck Webcam Opened", "Yes");

                duckWebcam.setPipeline(duckPipeline);
                duckWebcam.startStreaming(640, 360, OpenCvCameraRotation.UPRIGHT);
                telemetry.addData("duck webcam startStreaming", "yes");
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addLine("Can't open duck cameras");
                telemetry.update();
                /*
                 * This will be called if the camera could not be opened
                 */
            }
        });

        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                /*
                 * Tell the webcam to start streaming images to us! Note that you must make sure
                 * the resolution you specify is supported by the camera. If it is not, an exception
                 * will be thrown.
                 *
                 * Keep in mind that the SDK's UVC driver (what OpenCvWebcam uses under the hood) only
                 * supports streaming from the webcam in the uncompressed YUV image format. This means
                 * that the maximum resolution you can stream at and still get up to 30FPS is 480p (640x480).
                 * Streaming at e.g. 720p will limit you to up to 10FPS and so on and so forth.
                 *
                 * Also, we specify the rotation that the webcam is used in. This is so that the image
                 * from the camera sensor can be rotated such that it is always displayed with the image upright.
                 * For a front facing camera, rotation is defined assuming the user is looking at the screen.
                 * For a rear facing camera or a webcam, rotation is defined assuming the camera is facing
                 * away from the user.
                 */
                telemetry.addData("front webcam open", "yes");
                webcam.setPipeline(pipeline);
                webcam.startStreaming(640, 360, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
                telemetry.addLine("Can't open front camera");
                telemetry.update();
                /*
                 * This will be called if the camera could not be opened
                 */
            }
        });
    }

//    Functions for stopping cameras and switching to a pipeline that don't burn cpu cycles.
    public void stopDuckCamera(){

        duckWebcam.stopStreaming();
        duckWebcam.setPipeline(new DoNothingPipeline());
    }

    public void stopShippingElementCamera(){
        webcam.stopStreaming();
        webcam.setPipeline(new DoNothingPipeline());
    }

//    The aforementioned pipeline. Aptly named.
    public static class DoNothingPipeline extends OpenCvPipeline{

        @Override
        public Mat processFrame(Mat input) {
            return null;
        }
    }

//Pipeline for finding duck
    public static class DuckDeterminationPipeline extends OpenCvPipeline{
        Telemetry telemetry;
        public DuckDeterminationPipeline(Telemetry telemetry) {
            this.telemetry = telemetry;
        }

//    All possible regions to be detected in are stored in an enum
        public enum DuckPosition {
            LEFT5,
            LEFT4,
            LEFT3,
            LEFT2,
            LEFT1,
            CENTER,
            RIGHT1,
            RIGHT2,
            RIGHT3,
            RIGHT4,
            RIGHT5,
            SHRUG_NOISES
        }


        /*
         * Some color constants used for displaying rectangles on the camera stream
         */
        static final Scalar BLUE = new Scalar(0, 0, 255);
//        static final Scalar GREEN = new Scalar(0, 255, 0);
//        static final Scalar RED = new Scalar(255, 0, 0); e


//        Sizes for subregions of the camera from which our data is extracted
        static final int REGION_WIDTH = 100;
        static final int REGION_HEIGHT = 100;

        /*
         * List for the storage of points, if you're only dealing with a few regions declare them all separately, the freight regions in the other pipeline
         * are done like this.
         */

        ArrayList<Point> topLeftPoints = new ArrayList<>();
        ArrayList<Point> bottomRightPoints = new ArrayList<>();


//        The threshold to which the averages are compared.
        final int DUCK_PRESENT_THRESHOLD = 140;



        /*
         * Empty matrices that data will be stored in
         */

        Mat LAB = new Mat();
        Mat A = new Mat();
        Mat B = new Mat();

        // Volatile since accessed by OpMode thread w/o synchronization
        public volatile DuckPosition position = DuckPosition.CENTER;

        /*
         * This function takes the RGB frame, converts to LAB,
         * and extracts the A channel to the 'A' variable
         */
        void inputToLAB(Mat input) {

            Imgproc.cvtColor(input, LAB, Imgproc.COLOR_RGB2Lab);
            Core.extractChannel(LAB, A, 1);
            Core.extractChannel(LAB, B, 2);
        }

//        Done in innit, this was a more complicated formation of subregions for detecting the duck, but essentially
//        just assign the top left and bottom right points for each region you desire.
        @Override
        public void init(Mat firstFrame) {

            topLeftPoints.add(new Point(20,30));
            bottomRightPoints.add(new Point(topLeftPoints.get(0).x + REGION_WIDTH, topLeftPoints.get(0).y + REGION_HEIGHT));

            int tempY = 30;
            int tempX = 20;
            for (int i = 1; i < 33; i++) {
                if (tempX == 520) {
                    tempX = 20;
                    tempY += REGION_HEIGHT;
                } else {
                    tempX += 50;
                }
                topLeftPoints.add(new Point(tempX,tempY));
                bottomRightPoints.add(new Point(tempX + REGION_WIDTH, tempY + REGION_HEIGHT));
            }

            inputToLAB(firstFrame);


        }

//        Process frame, takes a matrix input and processes it. I still have no idea WHERE this is called, but it is absolutely essential to CV functioning
        @Override
        public Mat processFrame(Mat input) {
            inputToLAB(input);

//            Declare list of regions
            ArrayList<Mat> region = new ArrayList<>();


//            Put the necessary data from the frame to each region
            for (int i = 0; i<33; i++) {
                region.add(B.submat(new Rect(topLeftPoints.get(i), bottomRightPoints.get(i))));
            }

            ArrayList<Integer> regionAvgs = new ArrayList<>();

            for (int i = 0; i<33; i++) {
                regionAvgs.add((int) Core.mean(region.get(i)).val[0]);
            }

//              This is what displays the rectangles to the camera stream on the drive hub
            for (int i = 0; i<33; i++) {
                Imgproc.rectangle(
                        input, // Buffer to draw on
                        topLeftPoints.get(i), // First point which defines the rectangle
                        bottomRightPoints.get(i), // Second point which defines the rectangle
                        BLUE, // The color the rectangle is drawn in
                        2); // Thickness of the rectangle lines

            }

//            The next several lines determine which region has the highest B average.
            int indexOfMaximumBAvg = 0;

            for (int i = 0; i<33; i++) {
                if (regionAvgs.get(i) >= regionAvgs.get(indexOfMaximumBAvg)) {
                    indexOfMaximumBAvg = i;
                }
            }

//            Display telemetry
            telemetry.addData("B Averages", regionAvgs);
            telemetry.addData("Index of highest likelihood.", indexOfMaximumBAvg);

//            Fix indexes (Regions are stacked 11 to a row)
            if (regionAvgs.get(indexOfMaximumBAvg) >= DUCK_PRESENT_THRESHOLD) {
                if (indexOfMaximumBAvg > 21) {
                    indexOfMaximumBAvg -= 22;
                } else if (indexOfMaximumBAvg > 10) {
                    indexOfMaximumBAvg -= 11;
                }
                position = DuckPosition.values()[indexOfMaximumBAvg];
            } else {
                position = DuckPosition.SHRUG_NOISES; // Default enum result. Aptly named
            }


            telemetry.addData("Position", position);
            telemetry.update();


            return input;
        }

    }


    public static class ShippingElementDeterminationPipeline extends OpenCvPipeline {
        Telemetry telemetry;

        public ShippingElementDeterminationPipeline(Telemetry telemetry) {
            this.telemetry = telemetry;
        }

        /*
         * Enums to hold the various positions possible.
         */
        public enum ElementPosition {
            LEFT,
            MIDDLE,
            RIGHT
        }


        /*
         * Some color constants for display
         */
        static final Scalar BLUE = new Scalar(0, 0, 255);
        static final Scalar GREEN = new Scalar(0, 255, 0);
        static final Scalar RED = new Scalar(255, 0, 0);

//        Region sizes for freight submats
        static final int REGION_WIDTH = 30;
        static final int REGION_HEIGHT = 42;

        /*
        * Top left anchor points for freight regions         */
        static final Point REGION1_TOP_LEFT_ANCHOR_POINT = new Point(45, 180);
        static final Point REGION2_TOP_LEFT_ANCHOR_POINT = new Point(300, 180);
        static final Point REGION3_TOP_LEFT_ANCHOR_POINT = new Point(575, 180);

        ArrayList<Point> hubPositionArrayTopLeftPoint = new ArrayList<>();
        ArrayList<Point> hubPositionArrayBottomRightPoint = new ArrayList<>();

//      Threshold for freight presence
        final int FREIGHT_PRESENT_THRESHOLD = 112;

        //        Display rectangles to camera stream
        Point region1_pointA = new Point(
                REGION1_TOP_LEFT_ANCHOR_POINT.x,
                REGION1_TOP_LEFT_ANCHOR_POINT.y);
        Point region1_pointB = new Point(
                REGION1_TOP_LEFT_ANCHOR_POINT.x + REGION_WIDTH,
                REGION1_TOP_LEFT_ANCHOR_POINT.y + REGION_HEIGHT);
        Point region2_pointA = new Point(
                REGION2_TOP_LEFT_ANCHOR_POINT.x,
                REGION2_TOP_LEFT_ANCHOR_POINT.y);
        Point region2_pointB = new Point(
                REGION2_TOP_LEFT_ANCHOR_POINT.x + REGION_WIDTH,
                REGION2_TOP_LEFT_ANCHOR_POINT.y + REGION_HEIGHT);

        Point region3_pointA = new Point(
                REGION3_TOP_LEFT_ANCHOR_POINT.x,
                REGION3_TOP_LEFT_ANCHOR_POINT.y);
        Point region3_pointB = new Point(
                REGION3_TOP_LEFT_ANCHOR_POINT.x + REGION_WIDTH,
                REGION3_TOP_LEFT_ANCHOR_POINT.y + REGION_HEIGHT);


        /*
         * Working variables
         */
        Mat region1;
        Mat region2;
        Mat region3;
        Mat LAB = new Mat();

        Mat A = new Mat();
        Mat B = new Mat();

        int avg1 = 0;
        int avg2 = 0;
        int avg3 = 0;

        // Volatile since accessed by OpMode thread w/o synchronization
        public volatile ElementPosition position = ElementPosition.LEFT;

//        public volatile SkystoneDeterminationPipeline.HubPosition hub_position = SkystoneDeterminationPipeline.HubPosition.SHRUG_NOISES;

        /*
         * This function takes the RGB frame, converts to LAB,
         * and extracts both channels used.*/

        void inputToLAB(Mat input) {

            Imgproc.cvtColor(input, LAB, Imgproc.COLOR_RGB2Lab);
            Core.extractChannel(LAB, A, 1);
            Core.extractChannel(LAB, B, 2);
        }

        @Override
        public void init(Mat firstFrame) {

            hubPositionArrayTopLeftPoint.add(new Point(0,33));
            hubPositionArrayBottomRightPoint.add(new Point(hubPositionArrayTopLeftPoint.get(0).x + 20,hubPositionArrayTopLeftPoint.get(0).y + 50));

            for (int i=1; i<32; i++){
                hubPositionArrayTopLeftPoint.add(new Point(hubPositionArrayTopLeftPoint.get(i-1).x + 20,33));
                hubPositionArrayBottomRightPoint.add( new Point(hubPositionArrayBottomRightPoint.get(i-1).x + 20,hubPositionArrayTopLeftPoint.get(i).y + 50));
            }

            inputToLAB(firstFrame);

            region1 = A.submat(new Rect(region1_pointA, region1_pointB));
            region2 = A.submat(new Rect(region2_pointA, region2_pointB));
            region3 = A.submat(new Rect(region3_pointA, region3_pointB));
        }

        @Override
        public Mat processFrame(Mat input) {
            inputToLAB(input);
            region1 = A.submat(new Rect(region1_pointA, region1_pointB));
            region2 = A.submat(new Rect(region2_pointA, region2_pointB));
            region3 = A.submat(new Rect(region3_pointA, region3_pointB));

            avg1 = (int) Core.mean(region1).val[0];
            avg2 = (int) Core.mean(region2).val[0];
            avg3 = (int) Core.mean(region3).val[0];

            ArrayList<Mat> hubRegionRed = new ArrayList<>();
            ArrayList<Mat> hubRegionBlue = new ArrayList<>();


            for (int i = 0; i<32; i++) {
                hubRegionRed.add(A.submat(new Rect(hubPositionArrayTopLeftPoint.get(i), hubPositionArrayBottomRightPoint.get(i))));
                hubRegionBlue.add(B.submat(new Rect(hubPositionArrayTopLeftPoint.get(i), hubPositionArrayBottomRightPoint.get(i))));
            }

            ArrayList<Integer> hubRegionAvgRed = new ArrayList<>();
            ArrayList<Integer> hubRegionAvgBlue = new ArrayList<>();

            for (int i = 0; i<32; i++) {
                hubRegionAvgRed.add((int) Core.mean(hubRegionRed.get(i)).val[0]);
                hubRegionAvgBlue.add((int) Core.mean(hubRegionBlue.get(i)).val[0]);
            }

            Imgproc.rectangle(
                    input, // Buffer to draw on
                    region1_pointA, // First point which defines the rectangle
                    region1_pointB, // Second point which defines the rectangle
                    BLUE, // The color the rectangle is drawn in
                    2); // Thickness of the rectangle lines


            Imgproc.rectangle(
                    input, // Buffer to draw on
                    region2_pointA, // First point which defines the rectangle
                    region2_pointB, // Second point which defines the rectangle
                    GREEN, // The color the rectangle is drawn in
                    2); // Thickness of the rectangle lines

            Imgproc.rectangle(
                    input, // Buffer to draw on
                    region3_pointA, // First point which defines the rectangle
                    region3_pointB, // Second point which defines the rectangle
                    RED, // The color the rectangle is drawn in
                    2); // Thickness of the rectangle lines

            for (int i = 0; i<32; i++) {
                Imgproc.rectangle(
                        input, // Buffer to draw on
                        hubPositionArrayTopLeftPoint.get(i), // First point which defines the rectangle
                        hubPositionArrayBottomRightPoint.get(i), // Second point which defines the rectangle
                        BLUE, // The color the rectangle is drawn in
                        1); // Thickness of the rectangle lines

            }

            if (avg1 < FREIGHT_PRESENT_THRESHOLD) {
                position = ElementPosition.LEFT;
            } else if (avg2 < FREIGHT_PRESENT_THRESHOLD) {
                position = ElementPosition.MIDDLE;
            } else {
                position = ElementPosition.RIGHT;
            }
            telemetry.addData("Analysis", avg1);
            telemetry.addData("Analysis2", avg2);
            telemetry.addData("Analysis3", avg3);
            telemetry.addData("Position", position);

            int maxRedIndex = 0;


//            Nonfunctional! Logic problem not a syntax one.
            for (int i = 0; i<32; i++) {
                if (hubRegionAvgRed.get(i) > hubRegionAvgRed.get(maxRedIndex)) {
                    maxRedIndex = i;
                }
            }
            telemetry.addData("Blue Averages", hubRegionAvgBlue);
            telemetry.update();

            return input;
        }


    }public static class WarehousePipeline extends OpenCvPipeline{
        Telemetry telemetry;
        public WarehousePipeline(Telemetry telemetry) {
            this.telemetry = telemetry;
        }

        //    All possible regions to be detected in are stored in an enum
        public enum FreightPosition {
            LEFT5,
            LEFT4,
            LEFT3,
            LEFT2,
            LEFT1,
            CENTER,
            RIGHT1,
            RIGHT2,
            RIGHT3,
            RIGHT4,
            RIGHT5,
            SHRUG_NOISES
        }

        public enum FreightLocation {
            NEAR,
            MIDDLE,
            FAR,
            UNKNOWN
        }



        /*
         * Some color constants used for displaying rectangles on the camera stream
         */
        static final Scalar BLUE = new Scalar(0, 0, 255);


        //        Sizes for subregions of the camera from which our data is extracted
        static final int REGION_WIDTH = 120;
        static final int REGION_HEIGHT = 120;

        /*
         * List for the storage of points, if you're only dealing with a few regions declare them all separately, the freight regions in the other pipeline
         * are done like this.
         */

        ArrayList<Point> topLeftPoints = new ArrayList<>();
        ArrayList<Point> bottomRightPoints = new ArrayList<>();


        //        The threshold to which the averages are compared.
        final int DUCK_PRESENT_THRESHOLD = 160;



        /*
         * Empty matrices that data will be stored in
         */

        Mat LAB = new Mat();
        Mat A = new Mat();
        Mat B = new Mat();

        // Volatile since accessed by OpMode thread w/o synchronization
        public volatile WarehousePipeline.FreightPosition freightPosition = WarehousePipeline.FreightPosition.CENTER;
        public volatile WarehousePipeline.FreightLocation freightDistance = WarehousePipeline.FreightLocation.UNKNOWN;
        /*
         * This function takes the RGB frame, converts to LAB,
         * and extracts the A channel to the 'A' variable
         */
        void inputToLAB(Mat input) {

            Imgproc.cvtColor(input, LAB, Imgproc.COLOR_RGB2Lab);
            Core.extractChannel(LAB, A, 1);
            Core.extractChannel(LAB, B, 2);
        }

        //        Done in innit, this was a more complicated formation of subregions for detecting the duck, but essentially
//        just assign the top left and bottom right points for each region you desire.
        @Override
        public void init(Mat firstFrame) {

            topLeftPoints.add(new Point(20,1));
            bottomRightPoints.add(new Point(topLeftPoints.get(0).x + REGION_WIDTH, topLeftPoints.get(0).y + REGION_HEIGHT));

            int tempY = 0;
            int tempX = 20;
            for (int i = 1; i < 27; i++) {
                if (tempX == 500) {
                    tempX = 20;
                    tempY += REGION_HEIGHT;
                } else {
                    tempX += REGION_WIDTH/2;
                }
                topLeftPoints.add(new Point(tempX,tempY));
                bottomRightPoints.add(new Point(tempX + REGION_WIDTH, tempY + REGION_HEIGHT));
            }

            inputToLAB(firstFrame);


        }

        //        Process frame, takes a matrix input and processes it. I still have no idea WHERE this is called, but it is absolutely essential to CV functioning
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public Mat processFrame(Mat input) {
            inputToLAB(input);

//            Declare list of regions
            ArrayList<Mat> region = new ArrayList<>();


//            Put the necessary data from the frame to each region
            for (int i = 0; i<27; i++) {
                region.add(B.submat(new Rect(topLeftPoints.get(i), bottomRightPoints.get(i))));
            }

            ArrayList<Integer> regionAvgs = new ArrayList<>();

            for (int i = 0; i<27; i++) {
                regionAvgs.add((int) Core.mean(region.get(i)).val[0]);
            }

//              This is what displays the rectangles to the camera stream on the drive hub
            for (int i = 0; i<27; i++) {
                Imgproc.rectangle(
                        input, // Buffer to draw on
                        topLeftPoints.get(i), // First point which defines the rectangle
                        bottomRightPoints.get(i), // Second point which defines the rectangle
                        BLUE, // The color the rectangle is drawn in
                        2); // Thickness of the rectangle lines

            }

//            The next several lines determine which region has the highest B average.
            int indexOfMaximumBAvg = 0;

            for (int i = 0; i<27; i++) {
                if (regionAvgs.get(i) >= regionAvgs.get(indexOfMaximumBAvg)) {
                    indexOfMaximumBAvg = i;
                }
            }

//            Display telemetry
            telemetry.addData("B Averages", regionAvgs);
            telemetry.addData("Index of highest likelihood.", indexOfMaximumBAvg);

            List<Integer> nearRow = (List<Integer>) regionAvgs.subList(18,27);
            List<Integer> middleRow = (List<Integer>) regionAvgs.subList(9,18);
            List<Integer> farRow = (List<Integer>) regionAvgs.subList(0,9);



            if (nearRow.stream().mapToInt(v -> v).max().orElseThrow(NoSuchElementException::new) > DUCK_PRESENT_THRESHOLD) {
                indexOfMaximumBAvg = 0;
                for (int i = 0; i<9; i++) {
                    if (nearRow.get(i) >= regionAvgs.get(indexOfMaximumBAvg)) {
                        indexOfMaximumBAvg = i;
                    }
                }
                freightPosition = WarehousePipeline.FreightPosition.values()[indexOfMaximumBAvg];
                freightDistance = WarehousePipeline.FreightLocation.NEAR;
            } else if (middleRow.stream().mapToInt(v -> v).max().orElseThrow(NoSuchElementException::new) > DUCK_PRESENT_THRESHOLD) {
                indexOfMaximumBAvg = 0;
                for (int i = 0; i<9; i++) {
                    if (middleRow.get(i) >= regionAvgs.get(indexOfMaximumBAvg)) {
                        indexOfMaximumBAvg = i;
                    }
                }
                freightPosition = WarehousePipeline.FreightPosition.values()[indexOfMaximumBAvg];
                freightDistance = WarehousePipeline.FreightLocation.MIDDLE;
            } else if (farRow.stream().mapToInt(v -> v).max().orElseThrow(NoSuchElementException::new) > DUCK_PRESENT_THRESHOLD) {
                indexOfMaximumBAvg = 0;
                for (int i = 0; i<9; i++) {
                    if (farRow.get(i) >= regionAvgs.get(indexOfMaximumBAvg)) {
                        indexOfMaximumBAvg = i;
                    }
                }
                freightPosition = WarehousePipeline.FreightPosition.values()[indexOfMaximumBAvg];
                freightDistance = WarehousePipeline.FreightLocation.FAR;
            } else {
                freightPosition = WarehousePipeline.FreightPosition.SHRUG_NOISES;
            }


            telemetry.addData("Freight Position", freightPosition);
            telemetry.addData("Freight Distance", freightDistance);

            telemetry.update();

            return input;
        }


    }


}