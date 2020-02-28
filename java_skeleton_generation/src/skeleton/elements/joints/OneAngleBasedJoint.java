package skeleton.elements.joints;

import skeleton.elements.terminal.TerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public abstract class OneAngleBasedJoint extends Joint {

    private float minAngle;
    private float maxAngle;
    float currentAngle = 0f;

    TerminalElement child;

    public OneAngleBasedJoint(TerminalElement parent, Point3f position, float minAngle, float maxAngle) {
        super(parent, position);
        float eps = 0.01f;
        if (minAngle > maxAngle || Math.abs(minAngle) > Math.toRadians(180)+eps || Math.abs(maxAngle) > Math.toRadians(180)+eps) {
            System.err.println("Invalid angle");
        }
        if (!(minAngle <= 0f && maxAngle >= 0f)) {
            System.err.println("The initial position of this one angle joint is not at 0 degrees");
        }
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
    }

    /**
     * the turn direction (or null)
     * true: anti-clockwise
     * false: clockwise
     * @return null if no turn direction would bring foot nearer to floor
     */
    abstract Boolean getTurnDirectionNearerToFloor();

    public boolean movementPossible(boolean nearerToFloor) {
        Boolean turnDirection = getTurnDirectionNearerToFloor();
        if (turnDirection == null) {
            return !nearerToFloor;
        }
        return (turnDirection && currentAngle < maxAngle) || (!turnDirection && currentAngle > minAngle);
    }

    public void setNewAngle(boolean nearerToFloor, float stepSize) {
        Boolean turnDirection = getTurnDirectionNearerToFloor();
        if (turnDirection == null) {
            System.err.println("Cannot set new angle");
            return;
        }

        float sign = turnDirection ? 1f : -1f;
        if (!nearerToFloor) {
            sign = -sign;
        }
        currentAngle = currentAngle + sign * stepSize;
        if (currentAngle > maxAngle) {
            currentAngle = maxAngle;
        } else if (currentAngle < minAngle) {
            currentAngle = minAngle;
        }
    }

    /**
     * Uses an angle of 0° for the initial child.
     */
    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = new TransformationMatrix(new Vector3f(position));
        transform.rotateAroundZ(currentAngle);
        return transform;
    }

    public void setChild(TerminalElement child) {
        this.child = child;
    }
}
