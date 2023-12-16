package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;

import java.util.List;


public class CyDogsSparky extends CyDogsChassis{

    private LinearOpMode myOpMode;
    public SpikeCam spikeCam;
    public Direction parkingSpot;
    private boolean isElbowOpen = false;

    private Alliance myAlliance;

    private AprilTagProcessor aprilTag;

    private VisionPortal visionPortal;

    public static final int ArmHomePosition = 0;
    public static final int ArmLow = 1800;
    public static final int ArmMedium = 3600;
    public static final int ArmHigh = 6300;
    public static final int ArmRaiseBeforeElbowMovement = 3400;
    public static final double WristForDriving = 0.18;
    public static final double WristForScoring = 0.45;
    public static final double ElbowHomePosition = 0.233;
    public static final double ElbowScoringPosition = 0.5;
    public static final double FingerLeftOpen = 0.4;
    public static final double FingerLeftClosed = 0.5;
    public static final double FingerRightOpen = 0.4;
    public static final double FingerRightClosed = 0.53;
    public static final double DroneSecure = 0.53;
    public static final double DroneRelease = 0.3;

    public static final int BackUpDistanceFromSpike = 30;
    public static final int DistanceBetweenScoreBoardAprilTags = 150;

    public int StandardAutonWaitTime = 500;


    public Servo Wrist;
    public DcMotor ArmLift;
    public Servo DroneReleaseServo;
    public Servo Elbow;
    public Servo FingerLeft;
    public Servo FingerRight;
    public DcMotor Intake1;
    public DcMotor Intake2;
    public DcMotor TheCaptain;
    final double DESIRED_DISTANCE = 12.0;
    final double SPEED_GAIN  =  0.02  ;
    final double STRAFE_GAIN =  0.015 ;
    final double TURN_GAIN   =  0.01  ;
    final double MAX_AUTO_SPEED = 0.5;
    final double MAX_AUTO_STRAFE= 0.5;
    final double MAX_AUTO_TURN  = 0.3;




    public CyDogsSparky(LinearOpMode currentOp, Alliance currentAlliance, int standardWaitTime) {
        super(currentOp);
        myAlliance = currentAlliance;
        myOpMode = currentOp;
        StandardAutonWaitTime = standardWaitTime;
    }

    public void initializeSpikeCam(){
        spikeCam = new SpikeCam();
        WebcamName webcam1 = myOpMode.hardwareMap.get(WebcamName.class, "Webcam 1");
        OpenCvCamera spikeCamera = OpenCvCameraFactory.getInstance().createWebcam(webcam1);
        spikeCam.initialize(myOpMode, myAlliance, spikeCamera);
    }

    public void initializeDevices() {

        Wrist = myOpMode.hardwareMap.get(Servo.class, "Wrist");
        ArmLift = myOpMode.hardwareMap.get(DcMotor.class, "ArmLift");
        DroneReleaseServo = myOpMode.hardwareMap.get(Servo.class, "DroneRelease");
        Elbow = myOpMode.hardwareMap.get(Servo.class, "Elbow");
        FingerLeft = myOpMode.hardwareMap.get(Servo.class, "FingerLeft");
        FingerRight = myOpMode.hardwareMap.get(Servo.class, "FingerRight");


        // Initialize Drone Release and set position closed
        DroneReleaseServo.setDirection(Servo.Direction.FORWARD);

        // Initialize Arm Lift
        ArmLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        ArmLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ArmLift.setDirection(DcMotor.Direction.FORWARD);
        ArmLift.setPower(0.8);
        ArmLift.setTargetPosition(0);
        ArmLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // Initialize Finger
        FingerLeft.setDirection(Servo.Direction.FORWARD);
        FingerRight.setDirection(Servo.Direction.FORWARD);
        // Initialize Wrist
        Wrist.setDirection(Servo.Direction.FORWARD);
        // Initialize Elbow
        Elbow.setDirection(Servo.Direction.FORWARD);
    }

    public void initializePositions() {
        //DroneReleaseServo.setPosition(DroneSecure);
        Wrist.setPosition(WristForDriving);
        Elbow.setPosition(ElbowHomePosition);
        ArmLift.setTargetPosition(ArmHomePosition);
        FingerLeft.setPosition(FingerLeftClosed);
        FingerRight.setPosition(FingerRightClosed);
    }

    public void initializeAprilTags()
    {
        WebcamName webcam2 = myOpMode.hardwareMap.get(WebcamName.class, "Webcam 2");

        aprilTag = new AprilTagProcessor.Builder().build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(webcam2)
                .addProcessor(aprilTag)
                .build();
    }

    public void raiseArmToScore(int armHeight)
    {
    //    Finger.setPosition(FingerClosed);
        ArmLift.setPower(0.8);
        ArmLift.setTargetPosition(armHeight);
    }

    public void returnLiftForDriving()
    {
        ArmLift.setPower(0.8);
        ArmLift.setTargetPosition(ArmHomePosition);
    }

    public void LowerArmAtAutonEnd()
    {
        ArmLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        ArmLift.setPower(-0.4);
        myOpMode.sleep(1000);
        ArmLift.setPower(0);

    }

    public void openFingers()
    {
        FingerLeft.setPosition(FingerLeftOpen);
        FingerRight.setPosition(FingerRightOpen);
    }


    public void SetLiftToZero() {
        ArmLift.setPower(0.6);
        ArmLift.setTargetPosition(0);
    }
    public void SwingElbow() {
        if (ArmLift.getCurrentPosition() > ArmRaiseBeforeElbowMovement-20) {
            if (!isElbowOpen) {
                Elbow.setPosition(ElbowScoringPosition);
                Wrist.setPosition(WristForScoring);
                isElbowOpen = true;
            } else {
                Wrist.setPosition(WristForDriving);
                Elbow.setPosition(ElbowHomePosition);
                FingerLeft.setPosition(FingerLeftOpen);
                FingerRight.setPosition(FingerRightOpen);
                isElbowOpen = false;
            }
        }
    }

    public void scoreFromDrivingPositionAndReturn(){

        SwingElbow();
        myOpMode.sleep(800);
        raiseArmToScore(ArmLow);
        myOpMode.sleep(2000);
        openFingers();
        myOpMode.sleep(400);


    }
    public void returnArmFromScoring(){
        raiseArmToScore(ArmMedium);
        myOpMode.sleep(1700);
        SwingElbow();
        myOpMode.sleep(1500);
        returnLiftForDriving();
        myOpMode.sleep(2000);
    }

    public void AdjustToAprilTag(SpikeCam.location mySpike, String corner)
    {

        if(mySpike==SpikeCam.location.LEFT) {
            StrafeLeft(DistanceBetweenScoreBoardAprilTags, .5, 300);
        } else if (mySpike==SpikeCam.location.RIGHT) {
            StrafeRight(DistanceBetweenScoreBoardAprilTags+130,.5,300);
        }
        int targetTag = getAprilTagTarget(mySpike, myAlliance);
        myOpMode.sleep(400);

        double degreesBearing;
        double inchesXMovement;
        double inchesYMovement;
        // Adjust once
        AprilTagDetection foundTag = GetAprilTag(targetTag);
        // X
     //   if(foundTag != null) {
    //        inchesXMovement = foundTag.ftcPose.x;
     //       StrafeRight((int) (-(inchesXMovement)* 25.4), .5, 400);
     //   }
        // Bearing
     //   if(foundTag != null) {
      //      degreesBearing = foundTag.ftcPose.bearing;
      //      RotateRight((int)-degreesBearing,.5,400);
     //   }


        // Adjust a 2nd Time
      // foundTag = GetAprilTag(targetTag);
        // Bearing
        if(foundTag != null) {
            degreesBearing = foundTag.ftcPose.bearing;
            if(targetTag==3)
            {
                degreesBearing = degreesBearing*.9;
            }


            RotateRight((int)(-degreesBearing*.9),.5,300);
        }
     //   myOpMode.sleep(400);
     //   foundTag = GetAprilTag(targetTag);
        // X
        double extraInches =0;
        if(targetTag==6)
        {
            extraInches += 1.5;
        }
        if(targetTag==3){
            extraInches -= 0.0;
        }
        if(foundTag != null) {
            inchesXMovement = foundTag.ftcPose.x;
            StrafeRight((int) (-(inchesXMovement +extraInches)* 25.4), .5, 300);
        }

        // Y
        double adjustY = 6.5;
    //    if(targetTag==2)
      //  {
        //    adjustY = 6.5;

        //}
   //     myOpMode.sleep(400);
   //     foundTag = GetAprilTag(targetTag);
        if(foundTag != null) {
            inchesYMovement = foundTag.ftcPose.y;
            MoveStraight((int) (inchesYMovement * 25.4-adjustY*25.4), .5, 400);
            myOpMode.telemetry.addData("Target: ", getAprilTagTarget(mySpike, myAlliance));
            myOpMode.telemetry.addData("Bearing: ", foundTag.ftcPose.bearing);
            myOpMode.telemetry.addData("X adjustment: ", foundTag.ftcPose.x);
            myOpMode.telemetry.addData("Y adjustment: ", foundTag.ftcPose.y);
            myOpMode.telemetry.update();
        //    myOpMode.sleep(7000);
        }

        if(foundTag ==null) {
            MoveStraight(315,.5,450);
        } else {
            if (targetTag == 1 && corner == "BlueRight") {
                MoveStraight(7, .5, 400);
            }
            if (targetTag == 3 && corner == "BlueLeft") {
                MoveStraight(7, .5, 400);
            }
            if (targetTag == 3 && corner == "BlueRight") {
                StrafeRight(40, .5, 400);
            }
            if (targetTag == 4 && corner == "RedLeft") {
                StrafeLeft(30, .5, 400);
            }
            if (targetTag == 5 && corner == "RedLeft") {
                StrafeRight(20, .5, 400);
            }
        }

     //     myOpMode.sleep(8000);
    }

    private int getAprilTagTarget(SpikeCam.location mySpike, Alliance myAlliance)
    {
        int targetTag;
        if(myAlliance==Alliance.RED)
        {
            if(mySpike==SpikeCam.location.LEFT)
            {
                targetTag=4;
            } else if (mySpike== SpikeCam.location.MIDDLE) {
                targetTag=5;
            }
            else {   // RIGHT
                targetTag=6;
            }
        }
        else
        {
            if(mySpike==SpikeCam.location.LEFT)
            {
                targetTag=1;
            } else if (mySpike== SpikeCam.location.MIDDLE) {
                targetTag=2;
            }
            else {   // RIGHT
                targetTag=3;
            }
        }
        return targetTag;
    }

    public void dropPurplePixel(){
        FingerLeft.setPosition(FingerLeftOpen);
     //   FingerRight.setPosition(FingerRightOpen);
     //   this.MoveStraight(BackUpDistanceFromSpike,0.5,500);
    }

    public Direction askParkingSpot(){
        Direction parkingSpot = null;

        if (myOpMode.opModeInInit()) {
            while (myOpMode.opModeInInit()) {
                // Put loop blocks here.
                while (parkingSpot==null) {
                    myOpMode.telemetry.addLine("Driver,");
                    myOpMode.telemetry.addLine("To park LEFT of the backboard, press DPAD LEFT");
                    myOpMode.telemetry.addLine("To park RIGHT of the backboard, press DPAD RIGHT");
                    myOpMode.telemetry.update();
                    if (myOpMode.gamepad1.dpad_right) {
                       parkingSpot = Direction.RIGHT;
                        break;
                    }
                    if (myOpMode.gamepad1.dpad_left) {
                        parkingSpot = Direction.LEFT;
                        break;
                    }
                }
                while (!myOpMode.gamepad1.dpad_down) {
                    if (parkingSpot==Direction.LEFT) {
                        myOpMode.telemetry.addLine("Parking LEFT, Press Dpad Down to Confirm.");
                    } else if (parkingSpot==Direction.RIGHT) {
                        myOpMode.telemetry.addLine("Parking RIGHT, Press Dpad Down to Confirm.");
                    } else {
                        myOpMode.telemetry.addLine("Nothing selected, press Right Bumper to restart selection.");
                    }
                    myOpMode.telemetry.update();
                    if (myOpMode.gamepad1.dpad_down) {
                        break;
                    }
                }
                if (parkingSpot==Direction.LEFT) {
                    myOpMode. telemetry.addLine("Parking LEFT Confirmed.");
                    myOpMode.telemetry.update();
                    myOpMode.sleep(1000);
                } else if (parkingSpot==Direction.RIGHT) {
                    myOpMode. telemetry.addLine("Parking RIGHT Confirmed.");
                    myOpMode.telemetry.update();
                    myOpMode.sleep(1000);
                } else {
                    myOpMode.telemetry.addLine("Nothing selected.");
                    myOpMode.telemetry.update();
                }
                if (parkingSpot != null) {
                    break;
                }
                break;
            }

        }
        return parkingSpot;

    }

    public Direction askDrivePath(){
        Direction drivePath = null;

        if (myOpMode.opModeInInit()) {
            while (myOpMode.opModeInInit()) {
                // Put loop blocks here.
                while (drivePath==null) {
                    myOpMode.telemetry.addLine("Driver,");
                    myOpMode.telemetry.addLine("To drive through left, press DPAD LEFT");
             //       myOpMode.telemetry.addLine("To drive through center, press DPAD UP");
                    myOpMode.telemetry.addLine("To drive through right, press DPAD RIGHT");
                    myOpMode.telemetry.update();
                    if (myOpMode.gamepad1.dpad_right) {
                        drivePath = Direction.RIGHT;
                        break;
                    }
                    if (myOpMode.gamepad1.dpad_left) {
                        drivePath = Direction.LEFT;
                        break;
                    }
                //    if (myOpMode.gamepad1.dpad_up) {
                //        drivePath = Direction.CENTER;
                //        break;
                 //   }
                }
                while (!myOpMode.gamepad1.dpad_down) {
                    if (drivePath==Direction.LEFT) {
                        myOpMode.telemetry.addLine("Driving under LEFT, Press Dpad Down to Confirm.");
                    } else if (drivePath==Direction.RIGHT) {
                        myOpMode.telemetry.addLine("Driving under RIGHT, Press Dpad Down to Confirm.");
                    } else if (drivePath==Direction.CENTER) {
                        myOpMode.telemetry.addLine("Driving under CENTER, Press Dpad Down to Confirm.");
                    } else {
                        myOpMode.telemetry.addLine("Nothing selected, press Right Bumper to restart selection.");
                    }
                    myOpMode.telemetry.update();
                    if (myOpMode.gamepad1.dpad_down) {
                        break;
                    }
                }
                if (drivePath==Direction.LEFT) {
                    myOpMode. telemetry.addLine("Driving under LEFT Confirmed.");
                    myOpMode.telemetry.update();
                    myOpMode.sleep(1000);
                } else if (drivePath==Direction.RIGHT) {
                    myOpMode. telemetry.addLine("Driving under RIGHT Confirmed.");
                    myOpMode.telemetry.update();
                    myOpMode.sleep(1000);
                } else if (drivePath==Direction.CENTER) {
                    myOpMode. telemetry.addLine("Driving under CENTER Confirmed.");
                    myOpMode.telemetry.update();
                    myOpMode.sleep(1000);
                } else {
                    myOpMode.telemetry.addLine("Driving under not selected.");
                    myOpMode.telemetry.update();
                }
                if (drivePath != null) {
                    break;
                }
                break;
            }

        }
        return drivePath;

    }

    public void AutonPlacePurplePixel(SpikeCam.location mySpike){
        if(mySpike==SpikeCam.location.LEFT){
            RotateLeft(94,.5,StandardAutonWaitTime);
            MoveStraight(-35,.5,200);
            dropPurplePixel();
        } else if (mySpike==SpikeCam.location.MIDDLE) {
            MoveStraight(70,.5,StandardAutonWaitTime);
            dropPurplePixel();
        } else {
            RotateLeft(-90,.5,StandardAutonWaitTime);
            MoveStraight(-5,.5,200);
            dropPurplePixel();
        }
    }

    public void AutonPrepareToTravelThroughCenter(SpikeCam.location mySpike, CyDogsChassis.Direction myPath) {
        // this complex function determines where it is based on spike location and sets it up
        //   in front of the right path through center
        int adjustHorizontalDistance = 0;
        int adjustVerticalDistance = 0;

        // First, let's get ourselves straight facing scoring area
        //   Then, adjust position.  Remember dropping purple pixel moved us back from spike 20mm
        if(mySpike== SpikeCam.location.LEFT){
            //Already facing the correct way
            //We're 'BackUpDistanceFromSpike' closer to scoreboard
            adjustVerticalDistance = -BackUpDistanceFromSpike;
        } else if (mySpike==SpikeCam.location.MIDDLE) {
            RotateLeft(90,.5,StandardAutonWaitTime);
            // We're 50mm further away from start position
            adjustHorizontalDistance = -50;
        } else {
            RotateRight(180,.5,StandardAutonWaitTime);
            // We're 'BackUpDistanceFromSpike' further from scoreboard
            adjustVerticalDistance = -BackUpDistanceFromSpike;
        }
        MoveStraight(adjustVerticalDistance,.5,StandardAutonWaitTime);

        // we want to go left by default
        if(myPath==Direction.RIGHT) {
            StrafeRight(OneTileMM+adjustHorizontalDistance,.5,StandardAutonWaitTime);
        } else if (myPath==Direction.CENTER) {
            // Should be aligned in the center already
        } else {
            StrafeLeft(OneTileMM-adjustHorizontalDistance,.5,StandardAutonWaitTime);
        }
    }

    public void AutonCenterOnScoreboardBasedOnPath(Direction myPath) {
        if(myPath==Direction.LEFT) {
            StrafeRight(OneTileMM,.5,StandardAutonWaitTime);
        } else if (myPath==Direction.CENTER) {
            // Should be aligned in the center already
        } else {
            StrafeLeft(OneTileMM+110,.5,StandardAutonWaitTime);
        }
    }

    public void AutonParkInCorrectSpot(SpikeCam.location mySpike, Direction myParkingSpot){
        int leftAdjustment = 0;
        int rightAdjustment = 0;

        // need to adjust distance based on which april tag we're in front of
        if(mySpike==SpikeCam.location.LEFT) {
            leftAdjustment = -DistanceBetweenScoreBoardAprilTags;
            rightAdjustment = DistanceBetweenScoreBoardAprilTags;
        } else if (mySpike==SpikeCam.location.MIDDLE) {
            // no adjustments needed
        } else {
            leftAdjustment = DistanceBetweenScoreBoardAprilTags;
            rightAdjustment = -DistanceBetweenScoreBoardAprilTags;
        }

        if(myParkingSpot==Direction.LEFT){
            StrafeLeft(OneTileMM+leftAdjustment+90,.5,StandardAutonWaitTime);
        } else if (myParkingSpot==Direction.CENTER) {
            // really shouldn't park in center, but if so, I guess we're here
        } else {
            StrafeRight(OneTileMM+rightAdjustment+80,.5,StandardAutonWaitTime);
        }
    }

    public AprilTagDetection GetAprilTag(int tagID)
    {
        telemetryAprilTag();
        // Get the latest AprilTag detections from the pipeline.
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();

        // Iterate over the detections and return the one that matches the specified ID.
        for (AprilTagDetection aprilTagDetection : currentDetections) {
            if (aprilTagDetection.id == tagID) {
                return aprilTagDetection;
            }
        }

        // If no matching detection is found, return null.
        return null;
    }

    /**
     * Add telemetry about AprilTag detections.
     */
    private void telemetryAprilTag() {

        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        myOpMode.telemetry.addData("# AprilTags Detected", currentDetections.size());

        // Step through the list of detections and display info for each one.
        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                myOpMode.telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                myOpMode.telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                myOpMode.telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                myOpMode.telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
            } else {
                myOpMode.telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                myOpMode.telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
            }


        }   // end for() loop

        // Add "key" information to telemetry
        //     myOpMode.telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        //    myOpMode.telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
        //     myOpMode.telemetry.addLine("RBE = Range, Bearing & Elevation");
        //    myOpMode.telemetry.update();

    }   // end method telemetryAprilTag()
}