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

    float lastFirstAngle = 0f;
    float lastSecondAngle = 0f;
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
        lastFirstAngle = currentFirstAngle;
        lastSecondAngle = currentSecondAngle;
        currentFirstAngle = (random.nextFloat() * (maxFirstAngle - minFirstAngle)) + minFirstAngle;
        currentSecondAngle = (random.nextFloat() * (maxSecondAngle - minSecondAngle)) + minSecondAngle;
    }

    public void setNewFirstAngle(boolean nearerToFloor, float stepSize) {
        lastFirstAngle = currentFirstAngle;
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
        lastSecondAngle = currentSecondAngle;
        List<Boolean> turnDirections = getTurnDirectionsNearerToFloor();
        if (nearerToFloor && turnDirections == null) {
            System.err.println("can't set new side angle");
            return;
        }
        float sign;
        if (turnDirections == null || turnDirections.get(1) == null) {
            float eps = 0.1f;
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

    public void resetCurrentFirstAngle() {
        currentFirstAngle = lastFirstAngle;
    }

    public void resetCurrentSecondAngle() {
        currentSecondAngle = lastSecondAngle;
    }

    public void setCurrentFirstAngle(float currentFirstAngle) {
        if (currentFirstAngle < minFirstAngle || currentFirstAngle > maxFirstAngle) {
            System.err.println("Invalid first angle to set");
        } else {
            this.lastFirstAngle = this.currentFirstAngle;
            this.currentFirstAngle = currentFirstAngle;
        }
    }

    public void setCurrentSecondAngle(float currentSecondAngle) {
        if (currentSecondAngle < minSecondAngle || currentSecondAngle > maxSecondAngle) {
            System.err.println("Invalid second angle to set");
        } else {
            this.lastSecondAngle = this.currentSecondAngle;
            this.currentSecondAngle = currentSecondAngle;
        }
    }

    public void setChild(TerminalElement child) {
        this.child = child;
    }
}
