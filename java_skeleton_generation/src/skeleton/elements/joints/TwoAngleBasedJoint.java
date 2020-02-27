package skeleton.elements.joints;

import skeleton.elements.terminal.TerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.List;

public abstract class TwoAngleBasedJoint extends Joint {

    float minFrontAngle;
    float maxFrontAngle;
    float minSideAngle;
    float maxSideAngle;

    float currentFrontAngle = 0f;
    float currentSideAngle = 0f;

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
    abstract List<Boolean> getTurnDirectionsNearerToFloor();

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
        if (side) {
            return turnDirections.get(0) != null;
        } else {
            return turnDirections.get(1) != null;
        }
    }

    public void setNewSideAngle(boolean nearerToFloor, float stepSize) {
        List<Boolean> turnDirections = getTurnDirectionsNearerToFloor();
        if (turnDirections == null) {
            System.err.println("can't set new side angle");
            return;
        }
        float sign = nearerToFloor ? 1f : -1f;
        currentSideAngle = currentSideAngle + sign * stepSize;
        if (currentSideAngle > maxSideAngle) {
            currentSideAngle = maxSideAngle;
        } else if (currentSideAngle < minSideAngle) {
            currentSideAngle = minSideAngle;
        }
    }

    public void setNewFrontAngle(boolean nearerToFloor, float stepSize) {
        List<Boolean> turnDirections = getTurnDirectionsNearerToFloor();
        if (turnDirections == null) {
            System.err.println("can't set new front angle");
            return;
        }
        float sign = nearerToFloor ? 1f : -1f;
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
}
