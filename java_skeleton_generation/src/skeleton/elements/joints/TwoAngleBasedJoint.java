package skeleton.elements.joints;

import skeleton.elements.terminal.TerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class TwoAngleBasedJoint extends Joint {

    float minFrontAngle;
    float maxFrontAngle;
    float minSideAngle;
    float maxSideAngle;

    float currentFrontAngle = 0f;
    float currentSideAngle = 0f;

    TerminalElement child;
    Random random = new Random();

    public TwoAngleBasedJoint(TerminalElement parent, Point3f position, float minFrontAngle, float maxFrontAngle, float minSideAngle, float maxSideAngle) {
        super(parent, position);
        if (minFrontAngle > maxFrontAngle || Math.abs(minFrontAngle) > Math.toRadians(180) || Math.abs(maxFrontAngle) > Math.toRadians(180) ||
            minSideAngle > maxSideAngle || Math.abs(minSideAngle) > Math.toRadians(180) || Math.abs(maxSideAngle) > Math.toRadians(180)) {
            System.err.println("Invalid angle");
        }
        if (!(minFrontAngle <= 0f && maxFrontAngle >= 0f) || !(minSideAngle <= 0f && maxSideAngle >= 0f)) {
            System.err.println("The initial position of this one angle joint is not at 0 degrees");
        }
        this.minFrontAngle = minFrontAngle;
        this.maxFrontAngle = maxFrontAngle;
        this.minSideAngle = minSideAngle;
        this.maxSideAngle = maxSideAngle;
    }

    /**
     * first entry in the list is the side turn direction (or null), second the front turn direction (or null)
     * true: anti-clockwise
     * false: clockwise
     * @return null if no turn direction would bring foot nearer to floor
     */
    List<Boolean> getTurnDirectionsNearerToFloor() {
        List<Boolean> turnDirections = new ArrayList<>(2);

        Vector3f testVectorChild = new Vector3f(0f, -1f, 0f);
        child.calculateWorldTransform().applyOnVector(testVectorChild);

        float eps = 0.01f;
        turnDirections.add(Math.abs(testVectorChild.x) > eps ? testVectorChild.x < 0 : null);
        turnDirections.add(Math.abs(testVectorChild.z) > eps ? testVectorChild.z > 0 : null); // todo why??

        if (turnDirections.get(0) == null && turnDirections.get(1) == null) {
            return null;
        }

        return turnDirections;
    }

    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = new TransformationMatrix(new Vector3f(position));
        transform.rotateAroundX(currentFrontAngle);
        transform.rotateAroundZ(currentSideAngle);

        return transform;
    }

    public boolean movementPossible(boolean nearerToFloor, boolean side) {
        List<Boolean> turnDirections = getTurnDirectionsNearerToFloor();
        if (turnDirections == null) {
            return !nearerToFloor;
        }
        boolean movementPossible = false;
        if (side && turnDirections.get(0) != null) {
            if (nearerToFloor) {
                movementPossible = (turnDirections.get(0) && currentSideAngle < maxSideAngle) ||
                        (!turnDirections.get(0) && currentSideAngle > minSideAngle);
            } else {
                movementPossible = (turnDirections.get(0) && currentSideAngle > minSideAngle) ||
                        (!turnDirections.get(0) && currentSideAngle < maxSideAngle);
            }
        } else if (!side && turnDirections.get(1) != null) {
            if (nearerToFloor) {
                movementPossible = (turnDirections.get(1) && currentFrontAngle < maxFrontAngle) ||
                        (!turnDirections.get(1) && currentFrontAngle > minFrontAngle);
            } else {
                movementPossible = (turnDirections.get(1) && currentFrontAngle > minFrontAngle) ||
                        (!turnDirections.get(1) && currentFrontAngle < maxFrontAngle);
            }
        }

        return movementPossible;
    }

    public void setNewSideAngle(boolean nearerToFloor, float stepSize) {
        List<Boolean> turnDirections = getTurnDirectionsNearerToFloor();
        if (nearerToFloor && turnDirections == null) {
            System.err.println("can't set new side angle");
            return;
        }
        float sign;
        if (turnDirections == null || turnDirections.get(0) == null) {
            float eps = 0.1f;
            if (Math.abs(currentSideAngle) - minSideAngle < eps) {
                sign = 1f;
            } else if (Math.abs(currentSideAngle) - maxSideAngle < eps) {
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
        currentSideAngle = currentSideAngle + sign * stepSize;
        if (currentSideAngle > maxSideAngle) {
            currentSideAngle = maxSideAngle;
        } else if (currentSideAngle < minSideAngle) {
            currentSideAngle = minSideAngle;
        }
    }

    public void setNewFrontAngle(boolean nearerToFloor, float stepSize) {
        List<Boolean> turnDirections = getTurnDirectionsNearerToFloor();
        if (nearerToFloor && turnDirections == null) {
            System.err.println("can't set new front angle");
            return;
        }
        float sign;
        if (turnDirections == null || turnDirections.get(1) == null) {
            float eps = 0.1f;
            if (Math.abs(currentFrontAngle) - minFrontAngle < eps) {
                sign = 1f;
            } else if (Math.abs(currentFrontAngle) - maxFrontAngle < eps) {
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

        currentFrontAngle = currentFrontAngle + sign * stepSize;
        if (currentFrontAngle > maxFrontAngle) {
            currentFrontAngle = maxFrontAngle;
        } else if (currentFrontAngle < minFrontAngle) {
            currentFrontAngle = minFrontAngle;
        }
    }

    public void setMaxSideAngle(float maxSideAngle) {
        this.maxSideAngle = maxSideAngle;
    }

    public void setCurrentSideAngle(float currentSideAngle) {
        this.currentSideAngle = currentSideAngle;
    }

    public void setChild(TerminalElement child) {
        this.child = child;
    }
}
