package frc.robot.commands;

import frc.robot.subsystems.drive.Drive;
import frc.robot.Robot;
import frc.robot.lib.trajectory.RedTrajectory;
import frc.robot.lib.trajectory.TrajectoryLoader;
import frc.robot.lib.trajectory.RedTrajectory.TrajectoryType;
import frc.robot.subsystems.ExampleSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public final class Autos {
	/** Example static factory for an autonomous command. */
	public static Command exampleAuto(ExampleSubsystem subsystem) {
		return Commands.sequence(subsystem.exampleMethodCommand(), new ExampleCommand(subsystem));
	}

	/** An auto that runs a single test trajectory. */
	public static Command driveAuto() {
		RedTrajectory traj = TrajectoryLoader.loadAutoTrajectory(TrajectoryType.PATHPLANNER, "zisen").get();
		if (Robot.isSimulation()) {
			return Drive.getInstance().runOnce(() -> Drive.getInstance().resetPose(traj.getInitialState().pose))
						.andThen(Drive.getInstance().prepareCommand(traj.getInitialState().speeds, 1))
						.andThen(Drive.getInstance().trajectoryCommand(traj));
		}
		return Drive.getInstance().prepareCommand(traj.getInitialState().speeds, 1)
					.andThen(Drive.getInstance().trajectoryCommand(traj));
	}
}
