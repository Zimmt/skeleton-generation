package skeleton.elements.joints;

import skeleton.elements.terminal.TerminalElement;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Random;

public class OneAngleBasedJoint extends Joint {

    private float minAngle;
    private float maxAngle;
    private Random random = new Random();

    public OneAngleBasedJoint(Point3f position, float minAngle, float maxAngle) {
        super(position);
        float eps = 0.01f;
        if (minAngle > maxAngle || Math.abs(minAngle) > Math.toRadians(180)+eps || Math.abs(maxAngle) > Math.toRadians(180)+eps) {
            System.err.println("Invalid angle");
        }
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
    }

    public TransformationMatrix calculateChildTransform(TerminalElement parent) {
        float angle = (random.nextFloat() * (maxAngle - minAngle)) + minAngle;

        TransformationMatrix transform = new TransformationMatrix(new Vector3f(position));
        transform.rotateAroundZ(angle);
        System.out.println("one angle rotation " + Math.toDegrees(angle));
        return transform;
    }

    public OneAngleBasedJoint calculateMirroredJoint(TerminalElement parent, TerminalElement mirroredParent) {
        return new OneAngleBasedJoint(position, maxAngle, minAngle);
    }
}
