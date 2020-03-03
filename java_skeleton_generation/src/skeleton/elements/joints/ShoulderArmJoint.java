package skeleton.elements.joints;

import skeleton.elements.terminal.TerminalElement;
import skeleton.elements.terminal.UpperArm;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public class ShoulderArmJoint extends XZAngleBasedJoint {

    private static float maxFrontAngle = (float) Math.toRadians(170);
    private static float minSideAngle = (float) -Math.toRadians(90);
    private static float maxSideAngle = (float) Math.toRadians(170);

    public ShoulderArmJoint(TerminalElement parent, Point3f position) {
        super(parent, position, 0f, maxFrontAngle, minSideAngle, maxSideAngle);
        currentSecondAngle = (float) Math.toRadians(90);
    }

    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = super.calculateChildTransform(childBoundingBox);
        transform.translate(UpperArm.getLocalTranslationFromJoint(childBoundingBox));
        return transform;
    }

    public ShoulderArmJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new ShoulderArmJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
