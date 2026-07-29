package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Chassis {
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private GoBildaPinpointDriver pinpoint;
    private double speedModifier;
    private static final double BOOST_SPEED = 1.0;
    private static final double REGULAR_SPEED = 0.8;

    public Chassis(HardwareMap hwMap) {
        frontLeft = hwMap.get(DcMotor.class, "front_left");
        frontRight = hwMap.get(DcMotor.class, "front_right");
        backLeft = hwMap.get(DcMotor.class, "back_left");
        backRight = hwMap.get(DcMotor.class, "back_right");
        pinpoint = hwMap.get(GoBildaPinpointDriver.class, "pinpoint");
        speedModifier = REGULAR_SPEED;
    }

    public void boostOn() {
        speedModifier = BOOST_SPEED;
    }
    public void boostOff() {
        speedModifier = REGULAR_SPEED;
    }

    private void drive(double fowardSpeed, double rightSpeed, double turnCWSpeed) {

        fowardSpeed *= speedModifier;
        rightSpeed *= speedModifier;
        turnCWSpeed *= speedModifier;

        //mecanum drive formula
        frontLeft.setPower(fowardSpeed + rightSpeed + turnCWSpeed);
        frontRight.setPower(fowardSpeed - rightSpeed - turnCWSpeed);
        backLeft.setPower(fowardSpeed - rightSpeed + turnCWSpeed);
        backRight.setPower(fowardSpeed + rightSpeed - turnCWSpeed);

    }

    public void driveFieldRelative(double fieldForwardSpeed, double fieldRightSpeed, double turnCWSpeed) {
        double theta = Math.atan2(fieldForwardSpeed, fieldRightSpeed) + (Math.PI / 2); //add pi/2 to correct angle
        double radius = Math.hypot(fieldForwardSpeed, fieldRightSpeed);

        double robotAngle = pinpoint.getHeading(AngleUnit.RADIANS);
        theta = AngleUnit.normalizeRadians(theta - robotAngle);

        double forwardSpeed = radius * Math.sin(theta);
        double rightSpeed = radius * Math.cos(theta);

        drive(forwardSpeed, rightSpeed, turnCWSpeed);
    }
}
