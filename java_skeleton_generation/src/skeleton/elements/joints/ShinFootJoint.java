package skeleton.elements.joints;

import skeleton.elements.terminal.Foot;
import skeleton.elements.terminal.TerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class ShinFootJoint extends TwoAngleBasedJoint {

    private static float minFrontAngle = (float) -Math.toRadians(45);
    private static float maxFrontAngle = (float) Math.toRadians(45);
    private static float minSideAngle = (float) -Math.toRadians(170);

    public ShinFootJoint(TerminalElement parent, Point3f position) {
        super(parent, position, minFrontAngle, maxFrontAngle, minSideAngle, 0f);
    }

    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = super.calculateChildTransform(childBoundingBox);
        transform.translate(Foot.getLocalTranslationFromJoint(childBoundingBox));
        return transform;
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

    public ShinFootJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new ShinFootJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
