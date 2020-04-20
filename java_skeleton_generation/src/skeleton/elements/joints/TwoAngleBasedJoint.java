package skeleton.elements.joints;

import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;
import java.util.List;
import java.util.Random;

public abstract class TwoAngleBasedJoint extends Joint {
    private static final float eps = 0.01f;

    float minFirstAngle;
    float maxFirstAngle;
    float minSecondAngle;
    float maxSecondAngle;

    float nextToLastFirstAngle = 0f;
    float nextToLastSecondAngle = 0f;
    float lastFirstAngle = 0f;
    float lastSecondAngle = 0f;
    float currentFirstAngle = 0f;
    float currentSecondAngle = 0f;

    TerminalElement child;
    Random random = new Random();

    public TwoAngleBasedJoint(TerminalElement parent, Point3f position, float minFirstAngle, float maxFirstAngle, float minSecondAngle, float maxSecondAngle) {
        super(parent, position);
        if (minFirstAngle > maxFirstAngle || minSecondAngle > maxSecondAngle) {
            System.err.println("Invalid angle");
        }
        this.minFirstAngle = minFirstAngle;
        this.maxFirstAngle = maxFirstAngle;
        this.minSecondAngle = minSecondAngle;
        this.maxSecondAngle = maxSecondAngle;
    }

    abstract List<Boolean> getTurnDirectionsNearerToFloor();

    public boolean movementPossible(boolean nearerToFloor, boolean second) {
        List<Boolean> turnDirections = getTurnDirectionsNearerToFloor();
        if (turnDirections == null) {
            return !nearerToFloor;
        }
        boolean movementPossible = false;
        if (!second && turnDirections.get(0) != null) {
            if (nearerToFloor) {
                movementPossible = (turnDirections.get(0) && currentFirstAngle < maxFirstAngle) ||
                        (!turnDirections.get(0) && currentFirstAngle > minFirstAngle);
            } else {
                movementPossible = (turnDirections.get(0) && currentFirstAngle > minFirstAngle) ||
                        (!turnDirections.get(0) && currentFirstAngle < maxFirstAngle);
            }
        } else if (second && turnDirections.get(1) != null) {
            if (nearerToFloor) {
                movementPossible = (turnDirections.get(1) && currentSecondAngle < maxSecondAngle) ||
                        (!turnDirections.get(1) && currentSecondAngle > minSecondAngle);
            } else {
                movementPossible = (turnDirections.get(1) && currentSecondAngle > minSecondAngle) ||
                        (!turnDirections.get(1) && currentSecondAngle < maxSecondAngle);
            }
        }

        return movementPossible;
    }

    public void setRandomAngles() {
        shiftFirstAngleState();
        shiftSecondAngleState();
        currentFirstAngle = (random.nextFloat() * (maxFirstAngle - minFirstAngle)) + minFirstAngle;
        currentSecondAngle = (random.nextFloat() * (maxSecondAngle - minSecondAngle)) + minSecondAngle;
    }

    public void setNewFirstAngle(boolean nearerToFloor, float stepSize) {
        shiftFirstAngleState();
        List<Boolean> turnDirections = getTurnDirectionsNearerToFloor();
        if (nearerToFloor && turnDirections == null) {
            System.err.println("can't set new front angle");
            return;
        }
        float sign;
        if (turnDirections == null || turnDirections.get(0) == null) {
            float eps = 0.1f;
            if (Math.abs(currentFirstAngle-minFirstAngle) < eps) {
                sign = 1f;
            } else if (Math.abs(currentFirstAngle-maxFirstAngle) < eps) {
                sign = -1f;
            } else {
                sign = random.nextFloat() > 0.5 ? 1f : -1f;
            }
        } else {
            sign = turnDirections.get(0) ? 1f : -1f;
            if (!nearerToFloor) {
                sign = -sign;
            }
        }

        currentFirstAngle = currentFirstAngle + sign * stepSize;
        if (currentFirstAngle > maxFirstAngle) {
            currentFirstAngle = maxFirstAngle;
        } else if (currentFirstAngle < minFirstAngle) {
            currentFirstAngle = minFirstAngle;
        }
    }

    public void setNewSecondAngle(boolean nearerToFloor, float stepSize) {
        shiftSecondAngleState();
        List<Boolean> turnDirections = getTurnDirectionsNearerToFloor();
        if (nearerToFloor && turnDirections == null) {
            System.err.println("can't set new side angle");
            return;
        }
        float sign;
        if (turnDirections == null || turnDirections.get(1) == null) {
            if (Math.abs(currentSecondAngle-minSecondAngle) < eps) {
                sign = 1f;
            } else if (Math.abs(currentSecondAngle-maxSecondAngle) < eps) {
                sign = -1f;
            } else {
                sign = random.nextFloat() > 0.5 ? 1f : -1f;
            }
        } else {
            sign = turnDirections.get(1) ? 1f : -1f;
            if (!nearerToFloor) {
                sign = -sign;
            }
        }
        currentSecondAngle = currentSecondAngle + sign * stepSize;
        if (currentSecondAngle > maxSecondAngle) {
            currentSecondAngle = maxSecondAngle;
        } else if (currentSecondAngle < minSecondAngle) {
            currentSecondAngle = minSecondAngle;
        }
    }

    public void resetFirstAngle() {
        currentFirstAngle = lastFirstAngle;
        lastFirstAngle = nextToLastFirstAngle;
    }

    public void resetSecondAngle() {
        currentSecondAngle = lastSecondAngle;
        lastSecondAngle = nextToLastSecondAngle;
    }

    public void resetFirstAngleTwice() {
        resetFirstAngle();
        resetFirstAngle();
    }

    public void resetSecondAngleTwice() {
        resetSecondAngle();
        resetSecondAngle();
    }

    public void setCurrentFirstAngle(float currentFirstAngle) {
        if (currentFirstAngle < minFirstAngle-eps || currentFirstAngle > maxFirstAngle+eps) {
            System.err.println("Invalid first angle to set, clipping to bounds...");
            this.currentFirstAngle = clipAngle(currentFirstAngle, minFirstAngle, maxFirstAngle);
        } else {
            shiftFirstAngleState();
            this.currentFirstAngle = currentFirstAngle;
        }
    }

    public void setCurrentSecondAngle(float currentSecondAngle) {
        if (currentSecondAngle < minSecondAngle-eps || currentSecondAngle > maxSecondAngle+eps) {
            System.err.println("Invalid second angle to set, clipping to bounds...");
            this.currentSecondAngle = clipAngle(currentSecondAngle, minSecondAngle, maxSecondAngle);
        } else {
            shiftSecondAngleState();
            this.currentSecondAngle = currentSecondAngle;
        }
    }

    public float getCurrentFirstAngle() {
        return currentFirstAngle;
    }

    public float getCurrentSecondAngle() {
        return currentSecondAngle;
    }

    public void setChild(TerminalElement child) {
        this.child = child;
    }

    private void shiftFirstAngleState() {
        nextToLastFirstAngle = lastFirstAngle;
        lastFirstAngle = currentFirstAngle;
    }

    private void shiftSecondAngleState() {
        nextToLastSecondAngle = lastSecondAngle;
        lastSecondAngle = currentSecondAngle;
    }

    private float clipAngle(float angle, float min, float max) {
        float clampedAngle = angle;
        clampedAngle = Math.max(min, clampedAngle);
        clampedAngle = Math.min(max, clampedAngle);
        return clampedAngle;
    }
}
