package skeleton.elements.joints;

import skeleton.elements.terminal.TerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

/**
 * Only used for pelvic leg joint and for shoulder arm joint
 */
public abstract class XZAngleBasedJoint extends TwoAngleBasedJoint {

    public XZAngleBasedJoint(TerminalElement parent, Point3f position, float minFirstAngle, float maxFirstAngle, float minSecondAngle, float maxSecondAngle) {
        super(parent, position, minFirstAngle, maxFirstAngle, minSecondAngle, maxSecondAngle);
        if (minFirstAngle < 0) {
            System.err.println("What kind of joint is this??");
        }
    }

    public TransformationMatrix calculateChildTransform(BoundingBox boundingBox) {
        TransformationMatrix transform = new TransformationMatrix(new Vector3f(position));
        transform.rotateAroundX(currentFirstAngle);
        transform.rotateAroundZ(currentSecondAngle);

        return transform;
    }

    /**
     * first entry in the list is the turn direction for the first angle (or null), second the turn direction for the second (or null)
     * (nearer to floor means nearer to vertical position)
     * true: anti-clockwise
     * false: clockwise
     * @return null if no turn direction would bring foot nearer to floor
     */
    public List<Boolean> getTurnDirectionsNearerToFloor() {
        List<Boolean> turnDirections = new ArrayList<>(2);

        Vector3f testVectorChild = new Vector3f(0f, -1f, 0f);
        child.calculateWorldTransform().applyOnVector(testVectorChild);

        float eps = 0.01f;
        if (Math.abs(testVectorChild.z) > eps) { // testVectorChild is > 0 (it can't be < 0)
            turnDirections.add(testVectorChild.y > 0);
        } else {
            turnDirections.add(testVectorChild.y > 0 ? true : null);
        }
        turnDirections.add(Math.abs(testVectorChild.x) > eps ? testVectorChild.x < 0 : null);

        if (turnDirections.get(0) == null && turnDirections.get(1) == null) {
            return null;
        }

        return turnDirections;
    }
}
