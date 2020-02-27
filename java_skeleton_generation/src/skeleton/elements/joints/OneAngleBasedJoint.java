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

    public abstract boolean movementPossible(boolean nearerToFloor);
    public abstract void setNewAngle(boolean nearerToFloor, float stepSize);

    /**
     * Uses an angle of 0° for the initial child.
     */
    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = new TransformationMatrix(new Vector3f(position));
        transform.rotateAroundZ(currentAngle);
        return transform;
    }
}
