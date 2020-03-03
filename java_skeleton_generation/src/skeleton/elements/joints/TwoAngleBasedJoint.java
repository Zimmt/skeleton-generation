package skeleton.elements.joints;

import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;
import java.util.List;
import java.util.Random;

public abstract class TwoAngleBasedJoint extends Joint {

    float minFirstAngle;
    float maxFirstAngle;
    float minSecondAngle;
    float maxSecondAngle;

    float currentFirstAngle = 0f;
    float currentSecondAngle = 0f;

    TerminalElement child;
    Random random = new Random();

    public TwoAngleBasedJoint(TerminalElement parent, Point3f position, float minFirstAngle, float maxFirstAngle, float minSecondAngle, float maxSecondAngle) {
        super(parent, position);
        float eps = 0.01f;
        if (minFirstAngle > maxFirstAngle || Math.abs(minFirstAngle) > Math.toRadians(180)+eps || Math.abs(maxFirstAngle) > Math.toRadians(180)+eps ||
            minSecondAngle > maxSecondAngle || Math.abs(minSecondAngle) > Math.toRadians(180)+eps || Math.abs(maxSecondAngle) > Math.toRadians(180)+eps) {
            System.err.println("Invalid angle");
        }
        this.minFirstAngle = minFirstAngle;
        this.maxFirstAngle = maxFirstAngle;
        this.minSecondAngle = minSecondAngle;
        this.maxSecondAngle = maxSecondAngle;
    }

    abstract List<Boolean> getTurnDirectionsNearerToFloor();

    public boolean movementPossible(boolean nearerToFloor, boolean side) {
        List<Boolean> turnDirections = getTurnDirectionsNearerToFloor();
        if (turnDirections == null) {
            return !nearerToFloor;
        }
        boolean movementPossible = false;
        if (side && turnDirections.get(0) != null) {
            if (nearerToFloor) {
                movementPossible = (turnDirections.get(0) && currentSecondAngle < maxSecondAngle) ||
                        (!turnDirections.get(0) && currentSecondAngle > minSecondAngle);
            } else {
                movementPossible = (turnDirections.get(0) && currentSecondAngle > minSecondAngle) ||
                        (!turnDirections.get(0) && currentSecondAngle < maxSecondAngle);
            }
        } else if (!side && turnDirections.get(1) != null) {
            if (nearerToFloor) {
                movementPossible = (turnDirections.get(1) && currentFirstAngle < maxFirstAngle) ||
                        (!turnDirections.get(1) && currentFirstAngle > minFirstAngle);
            } else {
                movementPossible = (turnDirections.get(1) && currentFirstAngle > minFirstAngle) ||
                        (!turnDirections.get(1) && currentFirstAngle < maxFirstAngle);
            }
        }

        return movementPossible;
    }

    public void setNewFirstAngle(boolean nearerToFloor, float stepSize) {
        List<Boolean> turnDirections = getTurnDirectionsNearerToFloor();
        if (nearerToFloor && turnDirections == null) {
            System.err.println("can't set new front angle");
            return;
        }
        float sign;
        if (turnDirections == null || turnDirections.get(1) == null) {
            float eps = 0.1f;
            if (Math.abs(currentFirstAngle) - minFirstAngle < eps) {
                sign = 1f;
            } else if (Math.abs(currentFirstAngle) - maxFirstAngle < eps) {
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

        currentFirstAngle = currentFirstAngle + sign * stepSize;
        if (currentFirstAngle > maxFirstAngle) {
            currentFirstAngle = maxFirstAngle;
        } else if (currentFirstAngle < minFirstAngle) {
            currentFirstAngle = minFirstAngle;
        }
    }

    public void setNewSecondAngle(boolean nearerToFloor, float stepSize) {
        List<Boolean> turnDirections = getTurnDirectionsNearerToFloor();
        if (nearerToFloor && turnDirections == null) {
            System.err.println("can't set new side angle");
            return;
        }
        float sign;
        if (turnDirections == null || turnDirections.get(0) == null) {
            float eps = 0.1f;
            if (Math.abs(currentSecondAngle) - minSecondAngle < eps) {
                sign = 1f;
            } else if (Math.abs(currentSecondAngle) - maxSecondAngle < eps) {
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
        currentSecondAngle = currentSecondAngle + sign * stepSize;
        if (currentSecondAngle > maxSecondAngle) {
            currentSecondAngle = maxSecondAngle;
        } else if (currentSecondAngle < minSecondAngle) {
            currentSecondAngle = minSecondAngle;
        }
    }

    public void setCurrentFirstAngle(float currentFirstAngle) {
        this.currentFirstAngle = currentFirstAngle;
    }

    public void setCurrentSecondAngle(float currentSecondAngle) {
        this.currentSecondAngle = currentSecondAngle;
    }

    public void setChild(TerminalElement child) {
        this.child = child;
    }
}
