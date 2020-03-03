package skeleton.elements.joints;

import skeleton.elements.terminal.TerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Random;

public abstract class OneAngleBasedJoint extends Joint {

    float minAngle;
    float maxAngle;
    float currentAngle = 0f;

    TerminalElement child;
    Random random = new Random();

    public OneAngleBasedJoint(TerminalElement parent, Point3f position, float minAngle, float maxAngle) {
        super(parent, position);
        float eps = 0.01f;
        if (minAngle > maxAngle || Math.abs(minAngle) > Math.toRadians(180)+eps || Math.abs(maxAngle) > Math.toRadians(180)+eps) {
            System.err.println("Invalid angle");
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
    private Boolean getTurnDirectionNearerToFloor() {
        Vector3f testVectorParent = new Vector3f(0f, -1f, 0f);
        parent.calculateWorldTransform().applyOnVector(testVectorParent);
        Vector3f testVectorWorld = new Vector3f(0f, -1f, 0f);

        float eps = 0.01f;
        float wantedAngle = testVectorWorld.angle(testVectorParent);
        if (Math.abs(wantedAngle - currentAngle) < eps) {
            return null;
        } else {
            return currentAngle < wantedAngle;
        }
    }

    public boolean movementPossible(boolean nearerToFloor) {
        Boolean turnDirection = getTurnDirectionNearerToFloor();
        if (turnDirection == null) {
            return !nearerToFloor;
        }
        return (turnDirection && currentAngle < maxAngle) || (!turnDirection && currentAngle > minAngle);
    }

    public void setNewAngle(boolean nearerToFloor, float stepSize) {
        Boolean turnDirection = getTurnDirectionNearerToFloor();
        if (nearerToFloor && turnDirection == null) {
            System.err.println("Cannot set new angle");
            return;
        }

        float sign;
        if (turnDirection == null) {
            float eps = 0.1f;
            if (Math.abs(currentAngle) - minAngle < eps) {
                sign = 1f;
            } else if (Math.abs(currentAngle) - maxAngle < eps) {
                sign = -1f;
            } else {
                sign = random.nextFloat() > 0.5 ? 1f : -1f;
            }
        } else {
            sign = turnDirection ? 1f : -1f;
            if (!nearerToFloor) {
                sign = -sign;
            }
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

    public void setCurrentAngle(float currentAngle) {
        this.currentAngle = currentAngle;
    }

    public float getCurrentAngle() {
        return currentAngle;
    }

    public void setChild(TerminalElement child) {
        this.child = child;
    }
}
