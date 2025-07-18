package frc.robot.lib.control;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.Pair;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.lib.control.ControlConstants.*;

public class PIDVController implements Controller<Pair<Double, Double>, Double> {
    private final PIDFConstants constants;

    private double target = 0.0;
    private Pair<Double, Double> measurement = new Pair<>(0.0, 0.0);
    private double feedforward = 0.0;
    private double integral = 0.0;

    public double error = 0.0;

    private boolean isContinuous = false;
    private double minRange = 0.0;
    private double maxRange = 0.0;

    private final Timer timer = new Timer();

    /**
     * Creates a PIDV controller, which is a PID controller 
     * where the derivative is replaced by accurate velocity measurements.
     * @param constants The {@link PIDFConstants}.
     */
    public PIDVController(PIDFConstants constants) {
        this.constants = constants;
        timer.start();
    }

    public PIDVController(PIDConstants constants) {
        this(new PIDFConstants(constants));
    }
    /**
     * Makes the controller continuous, which means that values repeat.
     * @param minInput The minimum value.
     * @param maxInput The maximum value.
     */
    public void enableContinuousInput(double minInput, double maxInput) {
        isContinuous = true;
        minRange = minInput;
        maxRange = maxInput;
    }

    /** Makes the controller discontinuous */
    public void disableContinuousInput() {
        isContinuous = false;
    }

    @Override
    public void setInput(Pair<Double, Double> input) {
        this.measurement = input;
    }

    @Override
    public void setTarget(Double target) {
        this.target = target;
    }

    /** Sets the feedforward value. */
    public void setFeedforward(Double feedforward) {
        this.feedforward = feedforward;
    }

    @Override
    public Double getOutput() {
        double dt = timer.get();
        timer.reset();
        if (dt <= 0.0) return 0.0;
        
        double position = measurement.getFirst();
        double velocity = measurement.getSecond();

        error = isContinuous
            ? MathUtil.inputModulus(target - position, -(maxRange - minRange) / 2.0, (maxRange - minRange) / 2.0)
            : target - position;

        integral += error * dt;

        double derivative = feedforward - velocity;

        return constants.kP * error
             + constants.kI * integral
             + constants.kD * derivative
             + constants.kF * feedforward;
    }

    /** Sets the integral value. */
    public void setIntegral(double integral) {
        this.integral = integral;
    }

    /** Resets the controller. */
    public void reset() {
        integral = 0.0;
        feedforward = 0.0;
        error = 0.0;
        timer.reset();
        timer.start();
    }
}
