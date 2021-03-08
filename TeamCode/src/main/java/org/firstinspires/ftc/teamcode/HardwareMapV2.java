package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;
import java.util.Arrays;

public class HardwareMapV2 {
    public DcMotor frontRight = null, frontLeft = null, backRight = null, backLeft = null, intake = null, outtake = null;

    public CRServo conveyor = null;
    public Servo leftTilt = null, rightTilt = null, wobble = null;
    boolean odometry = false;
    boolean odometryTest = false;

    ArrayList<DcMotor> motors = new ArrayList<>(Arrays.asList(frontRight, frontLeft, backLeft, backRight, intake, outtake));
    ArrayList<DcMotor> odomotors = new ArrayList<>(Arrays.asList(frontRight, frontLeft, intake));
    ArrayList<? extends HardwareDevice> servos = new ArrayList<>(Arrays.asList(conveyor, leftTilt, rightTilt, wobble));

    ModernRoboticsI2cGyro realgyro1;
    HardwareMap hwMap = null;

    public HardwareMapV2 (){

    }

    public void init (HardwareMap awhMap) {

        hwMap = awhMap;
        frontLeft = hwMap.get(DcMotor.class, "front_left");
        frontRight = hwMap.get(DcMotor.class, "front_right");
        backRight = hwMap.get(DcMotor.class, "back_right");
        backLeft = hwMap.get(DcMotor.class, "back_left");
        intake = hwMap.get(DcMotor.class, "succ");
        outtake = hwMap.get(DcMotor.class, "spit");

        if (odometry) {

        }
        if (odometryTest){

        }
        conveyor = hwMap.get(CRServo.class, "convey");
        leftTilt = hwMap.get(Servo.class, "left_tilt");
        rightTilt = hwMap.get(Servo.class, "right_tilt");
        wobble = hwMap.get(Servo.class, "wobble");

        frontLeft.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        frontRight.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors
        backRight.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        intake.setDirection(DcMotor.Direction.REVERSE);
        outtake.setDirection(DcMotor.Direction.REVERSE);

        if (odometry) {

        }
        if (odometryTest){
            intake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            intake.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
        //conveyor.setDirection(DcMotorSimple.Direction.FORWARD);
        leftTilt.setDirection(Servo.Direction.REVERSE);
        rightTilt.setDirection(Servo.Direction.FORWARD);
        conveyor.setDirection(CRServo.Direction.REVERSE);
        wobble.setDirection(Servo.Direction.FORWARD);

        rightTilt.setPosition(0.5);
        leftTilt.setPosition(0.5);
        wobble.setPosition(0.0);

    }

    public void setEncoders(ArrayList<DcMotor> motors, DcMotor.RunMode mode){
        for (DcMotor motor: motors){
            motor.setMode(mode);
        }
    }

    public void setEncoders(DcMotor.RunMode mode){
        setEncoders(motors, mode);
        setEncoders(odomotors, mode);
    }

    public void setPowerAll(double power){
        for (DcMotor motor: motors){
            motor.setPower(power);
        }
    }

}
