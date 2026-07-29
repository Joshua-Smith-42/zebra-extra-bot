package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Mechanisms.Chassis;
import org.firstinspires.ftc.teamcode.Mechanisms.DifferentialIntakeArm;


public class TeleOp extends OpMode {
    public static final double TRIGGER_THRESHOLD = 0.2;
    Chassis chassis;
    DifferentialIntakeArm arm;

    @Override
    public void init() {
        chassis = new Chassis(hardwareMap);
        arm = new DifferentialIntakeArm(hardwareMap);
    }

    @Override
    public void loop() {
        arm.update(telemetry);
        chassis.driveFieldRelative(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        if (gamepad1.left_stick_button) {
            chassis.boostOn();
        } else {
            chassis.boostOff();
        }

        if (gamepad1.left_trigger > TRIGGER_THRESHOLD) {
            arm.intake();
        } else if (gamepad1.right_trigger > TRIGGER_THRESHOLD) {
            arm.outtake();
        } else {
            arm.intakeOff();
        }

        if (gamepad1.a) {
            arm.armGoTo(DifferentialIntakeArm.ArmPosistions.INTAKE);
        } else if (gamepad1.b) {
            arm.armGoTo(DifferentialIntakeArm.ArmPosistions.OUTTAKE);
        } else if (gamepad1.y) {
            arm.armGoTo(DifferentialIntakeArm.ArmPosistions.TRANSFER);
        }
    }
}
