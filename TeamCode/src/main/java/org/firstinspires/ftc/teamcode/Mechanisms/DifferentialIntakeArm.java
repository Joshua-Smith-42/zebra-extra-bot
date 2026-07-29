package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class DifferentialIntakeArm {
    //TODO: make posistions real values
    private static final int INTAKE_POS = 0;
    private static final int TRANSFER_POS = 40;
    private static final int OUTTAKE_POS = 250;
    private static final double INTAKE_SPEED = 1.0;
    private static final double KP = 0.005;
    private final DcMotor leftMotor;
    private final DcMotor rightMotor;
    private final RevColorSensorV3 leftSensor;
    private final RevColorSensorV3 rightSensor;
    private final RevTouchSensor limitSwitch;
    private double leftPower;
    private double rightPower;
    private int currentArmPos;
    private int desiredArmPos;
    private double intakeModifier;

    public DifferentialIntakeArm(HardwareMap hwMap) {
        leftMotor = hwMap.get(DcMotor.class, "left_arm");
        rightMotor = hwMap.get(DcMotor.class, "right_motor");
        leftSensor = hwMap.get(RevColorSensorV3.class, "left_sensor");
        rightSensor = hwMap.get(RevColorSensorV3.class, "right_sensor");
        limitSwitch = hwMap.get(RevTouchSensor.class, "limit_switch");

        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        leftPower = 0;
        rightPower = 0;
        currentArmPos = 0;
        desiredArmPos = 0;
        intakeModifier = 0;
    }

    private boolean leftFull() {
        return leftSensor.getDistance(DistanceUnit.MM) < 5;
    }

    private boolean rightFull() {
        return rightSensor.getDistance(DistanceUnit.MM) < 5;
    }

    public boolean outtakeFull() {
        return rightFull() && leftFull();
    }

    public void intake() {
        intakeModifier = 1;
    }

    public void outtake() {
        intakeModifier = -1;
    }

    public void intakeOff() {
        intakeModifier = 0;
    }

    private void setDesiredArmPos(int newPos) {
        desiredArmPos = newPos;
    }

    public void goTo(ArmPosistions pos) {
        switch (pos) {
            case INTAKE:
                setDesiredArmPos(INTAKE_POS);
            case OUTTAKE:
                setDesiredArmPos(OUTTAKE_POS);
            case TRANSFER:
                setDesiredArmPos(TRANSFER_POS);
        }
    }

    public void update(Telemetry telemetry) {
        if (limitSwitch.isPressed()) {
            leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            this.leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            this.rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        currentArmPos = (leftMotor.getCurrentPosition() + rightMotor.getCurrentPosition()) / 2; //average left and right
        double error = desiredArmPos - currentArmPos;
        double armPower = error * KP;
        if (desiredArmPos == 0 && !limitSwitch.isPressed()) {
            armPower = Math.min(-0.2, armPower);
        }

        leftPower = armPower + intakeModifier;
        rightPower = armPower - intakeModifier;

        leftMotor.setPower(leftPower);
        rightMotor.setPower(rightPower);

        telemetry.addData("current arm pos", currentArmPos);
        telemetry.addData("desired arm pos", desiredArmPos);
        telemetry.addData("error", error);
        telemetry.addData("power", armPower);
    }

    public enum ArmPosistions {
        INTAKE,
        TRANSFER,
        OUTTAKE
    }

}
