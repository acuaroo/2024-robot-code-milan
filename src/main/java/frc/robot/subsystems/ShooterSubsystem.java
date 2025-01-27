package frc.robot.subsystems;

import com.revrobotics.CANSparkBase;
import com.revrobotics.CANSparkFlex;
import com.revrobotics.CANSparkLowLevel;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.DoubleEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.constants.ShooterConstants;
import frc.robot.utils.MathUtils;

public class ShooterSubsystem extends SubsystemBase {
    private final CANSparkFlex topMotor = new CANSparkFlex(ShooterConstants.shooterTopMotorID, CANSparkLowLevel.MotorType.kBrushless);
    private final CANSparkFlex bottomMotor = new CANSparkFlex(ShooterConstants.shooterBottomMotorID, CANSparkLowLevel.MotorType.kBrushless);

    private final PIDController shooterPID = new PIDController(ShooterConstants.shooterP,ShooterConstants.shooterI, ShooterConstants.shooterD);

    private final DoubleEntry shooterSetpoint = NetworkTableInstance.getDefault()
            .getTable("Shooter").getDoubleTopic("Setpoint").getEntry(0.0);

    private final DoubleEntry shooterSpeedTop = NetworkTableInstance.getDefault()
            .getTable("Shooter").getDoubleTopic("Top").getEntry(0.0);

    private final DoubleEntry shooterSpeedBottom = NetworkTableInstance.getDefault()
            .getTable("Shooter").getDoubleTopic("Bottom").getEntry(0.0);


    /**
     * Subsystem for all shooter related things
     */
    public ShooterSubsystem(){
        topMotor.setInverted(true);
        bottomMotor.setInverted(true);
        topMotor.setIdleMode(CANSparkBase.IdleMode.kCoast);
        bottomMotor.setIdleMode(CANSparkBase.IdleMode.kCoast);
    }


    /**
     * Set the target speed for the shooter
     * @param speed target speed in rpm
     */
    public void setSpeed(double speed) {
        topMotor.setVoltage(shooterPID.calculate(MathUtils.rpmToRadians(topMotor.getEncoder().getVelocity()), MathUtils.rpmToRadians(speed)) +
                ShooterConstants.shooterFF.calculate(MathUtils.rpmToRadians(speed)));

        bottomMotor.setVoltage(shooterPID.calculate(MathUtils.rpmToRadians(bottomMotor.getEncoder().getVelocity()), MathUtils.rpmToRadians(speed)) +
                ShooterConstants.shooterFF.calculate(MathUtils.rpmToRadians(speed)));

        shooterSetpoint.set(speed);
    }

    /**
     * Get the RPM of the top motor
     * @return Returns RPM of the top motor
     */
    public double getTopMotorRPM(){
        return topMotor.getEncoder().getVelocity();
    }

    /**
     * Get the RPM of the bottom motor
     * @return Returns the RPM of the bottom motor
     */
    public double getBottomMotorRPM(){
        return bottomMotor.getEncoder().getVelocity();
    }

    /**
     * Sets the motors raw voltage input
     * @param topVolts Volts to give the top motor (-12-12)
     * @param bottomVolts Volts to give the bottom motor (-12-12)
     */
    public void setVoltage(double topVolts, double bottomVolts) {
        topMotor.setVoltage(topVolts);
        bottomMotor.setVoltage(bottomVolts);
    }

    /**
     * Set the voltage of both top and bottom motors
     * @param volts Target volts for the motors
     */
    public void setVoltage(double volts) {
        setVoltage(volts, volts);
    }

    @Override
    public void periodic() {
        shooterSpeedBottom.set(getBottomMotorRPM());
        shooterSpeedTop.set(getTopMotorRPM());
    }

}
