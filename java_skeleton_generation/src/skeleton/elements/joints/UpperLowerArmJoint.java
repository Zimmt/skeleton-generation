package skeleton.elements.joints;

import skeleton.elements.terminal.LowerArm;
import skeleton.elements.terminal.TerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public class UpperLowerArmJoint extends OneAngleBasedJoint {

    private static float min = (float) -Math.toRadians(170);
    private static float minWing = (float) -Math.toRadians(45);

    public UpperLowerArmJoint(TerminalElement parent, Point3f position) {
        super(parent, position, min, 0f);
        currentAngle = (float) -Math.toRadians(160);
    }

    public void setRandomWingAngle() {
        currentAngle = (random.nextFloat() * (maxAngle - minWing)) + minWing;
        System.out.println("upper lower arm angle: " + Math.toDegrees(currentAngle));
    }

    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = super.calculateChildTransform(childBoundingBox);
        transform.translate(LowerArm.getLocalTranslationFromJoint(childBoundingBox));
        return transform;
    }

    public UpperLowerArmJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new UpperLowerArmJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
