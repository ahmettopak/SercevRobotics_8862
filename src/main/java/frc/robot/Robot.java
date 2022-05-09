

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;


public class Robot extends TimedRobot {
  NetworkTableInstance chameleon = NetworkTableInstance.create();
  public static SendableChooser<Integer> autoChooser = new SendableChooser<>();
  private Command m_autonomousCommand;
  private RobotContainer m_robotContainer;
  public static NetworkTableEntry angle;
  public static NetworkTableInstance inst = NetworkTableInstance.getDefault();
  NetworkTable table = chameleon.getTable("chameleon-vision").getSubTable("Microsoft LifeCam HD-3000");
  public static NetworkTableEntry validAngle;

  @Override
  public void robotInit() {
    // Instantiate our RobotContainer. This will perform all our button bindings,
    // and put our
    // autonomous chooser on the dashboard.
    CameraServer server = CameraServer.getInstance();
    server.startAutomaticCapture();
    chameleon.startClient("10.72.85.12");
    angle = table.getEntry("targetYaw");
    validAngle = table.getEntry("isValid");
    autoChooser.setDefaultOption("3 Cell Straight", 0);
    autoChooser.addOption("Center Right 6 Cell", 1);
    autoChooser.addOption("Left 5 Cell", 2);
    SmartDashboard.putData("Autonomous Selector", autoChooser);
    m_robotContainer = new RobotContainer();

  }


  @Override
  public void robotPeriodic() {
    // Runs the Scheduler. This is responsible for polling buttons, adding
    // newly-scheduled
    // commands, running already-scheduled commands, removing finished or
    // interrupted commands,
    // and running subsystem periodic() methods. This must be called from the
    // robot's periodic
    // block in order for anything in the Command-based framework to work.

    CommandScheduler.getInstance().run();
    SmartDashboard.putBoolean("Vision Available", isVisionValid());
  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  /**
   * This autonomous runs the autonomous command selected by your
   * {@link RobotContainer} class.
   */
  @Override
  public void autonomousInit() {
    m_robotContainer.m_robotDrive.zeroHeading();
    m_robotContainer.m_robotDrive.resetEncoders();
    if (autoChooser.getSelected() == 2) {
      m_robotContainer.m_robotDrive.m_odometry
          .resetPosition(m_robotContainer.s_trajectory.leftAuto5Cell[0].getInitialPose(), new Rotation2d(0));
    } else {
      m_robotContainer.m_robotDrive.m_odometry
          .resetPosition(m_robotContainer.s_trajectory.centerRightAuto[0].getInitialPose(), new Rotation2d(0));
    }
    m_autonomousCommand = m_robotContainer.getAutonomousCommand(autoChooser.getSelected());
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }

  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }

  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  public static double getVisionYawAngle() {
    return angle.getDouble(0);
  }

  public static boolean isVisionValid() {
    return validAngle.getBoolean(false);
  }
}
