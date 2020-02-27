package skeleton.elements.joints;

import skeleton.elements.terminal.TerminalElement;
import skeleton.elements.terminal.Thigh;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class PelvicThighJoint extends TwoAngleBasedJoint {

    private static float maxFrontAngle = (float) Math.toRadians(170);
    private static float minSideAngle = (float) -Math.toRadians(170);

    private float sideAngleOffset; // the angle between local x axis and global x axis

    public PelvicThighJoint(TerminalElement parent, Point3f position) {
        super(parent, position, 0f, maxFrontAngle, minSideAngle, 0f);
        initializeSideAngle();
    }

    /**
     * child transform
     */
    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = super.calculateChildTransform(childBoundingBox);
        transform.translate(Thigh.getLocalTranslationFromJoint(childBoundingBox));
        return transform;
    }

    public PelvicThighJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new PelvicThighJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }

    /**
     * first entry in the list is the side turn direction (or null), second the front turn direction (or null)
     * true: anti-clockwise
     * false: clockwise
     * @return null if no turn direction would bring foot nearer to floor
     */
    List<Boolean> getTurnDirectionsNearerToFloor() {
        Vector3f testVector = new Vector3f(0f, -1f, 0f);
        parent.calculateWorldTransform().applyOnVector(testVector);
        float eps = 0.01f;

        if (Math.abs(testVector.y + 1f) < eps) {
            return null;
        }
        List<Boolean> turnDirections = new ArrayList<>(2);
        if (Math.abs(testVector.x) < eps) {
            turnDirections.add(null);
        } else {
            turnDirections.add(testVector.x < 0);
        }

        if (Math.abs(testVector.z) < eps) {
            turnDirections.add(null);
        } else {
            turnDirections.add(testVector.z < 0);
        }

        return turnDirections;
    }

    /**
     * Initializes side angle so that the coordinate system of the child is oriented like the world coordinate system
     */
    private void initializeSideAngle() {
        Vector3f horizontal = new Vector3f(1f, 0f, 0f);
        Vector3f localXDir = new Vector3f(1f, 0f, 0f);
        parent.calculateWorldTransform().applyOnVector(localXDir);
        this.sideAngleOffset = localXDir.angle(horizontal);
        if (localXDir.y > 0) {
            this.sideAngleOffset = -sideAngleOffset;
        }

        setMaxSideAngle(sideAngleOffset);
        setCurrentSideAngle(sideAngleOffset);
    }
}
