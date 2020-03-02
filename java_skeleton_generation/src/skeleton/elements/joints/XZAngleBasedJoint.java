package skeleton.elements.joints;

import skeleton.elements.terminal.TerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

public abstract class XZAngleBasedJoint extends TwoAngleBasedJoint {

    public XZAngleBasedJoint(TerminalElement parent, Point3f position, float minFirstAngle, float maxFirstAngle, float minSecondAngle, float maxSecondAngle) {
        super(parent, position, minFirstAngle, maxFirstAngle, minSecondAngle, maxSecondAngle);
    }

    public TransformationMatrix calculateChildTransform(BoundingBox boundingBox) {
        TransformationMatrix transform = new TransformationMatrix(new Vector3f(position));
        transform.rotateAroundX(currentFirstAngle);
        transform.rotateAroundZ(currentSecondAngle);

        return transform;
    }

    /**
     * first entry in the list is the side turn direction (or null), second the front turn direction (or null)
     * (nearer to floor means nearer to vertical position)
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
}
