package skeleton.elements.joints;

import skeleton.elements.terminal.Shin;
import skeleton.elements.terminal.TerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public class ThighShinJoint extends OneAngleBasedJoint {

    private static float maxAngle = (float) Math.toRadians(170);

    public ThighShinJoint(TerminalElement parent, Point3f position) {
        super(parent, position, 0f, maxAngle);
    }

    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = super.calculateChildTransform(childBoundingBox);
        transform.translate(Shin.getLocalTranslationFromJoint(childBoundingBox));
        return transform;
    }

    public boolean movementPossible(boolean nearerToFloor) {
        if (nearerToFloor) {
            return currentAngle > 0;
        } else {
            return currentAngle < maxAngle;
        }
    }

    public void setNewAngle(boolean nearerToFloor, float stepSize) {
        if (!movementPossible(nearerToFloor)) {
            System.err.println("Cannot set new angle as it is already set to max/min");
            return;
        }

        if (nearerToFloor) { // [0, currentAngle)
            if (currentAngle - stepSize > 0) {
                currentAngle -= stepSize;
            } else {
                currentAngle = 0f;
            }

        } else { // [currentAngle, maxAngle)
            if (currentAngle + stepSize < maxAngle) {
                currentAngle += stepSize;
            } else {
                currentAngle = maxAngle;
            }
        }
    }

    public ThighShinJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new ThighShinJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
