package skeleton.elements.joints;

import skeleton.elements.terminal.Hand;
import skeleton.elements.terminal.TerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public class LowerArmHandJoint extends OneAngleBasedJoint {

    private static float min = (float) -Math.toRadians(170);
    private static float max = (float) Math.toRadians(90);

    private static float minWing = 0f;

    public LowerArmHandJoint(TerminalElement parent, Point3f position) {
        super(parent, position, min, max);
        currentAngle = (float) -Math.toRadians(90);
    }

    public void setRandomWingAngle() {
        currentAngle = (random.nextFloat() * (max - minWing)) + minWing;
        System.out.println("hand angle: " + Math.toDegrees(currentAngle));
    }

    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = super.calculateChildTransform(childBoundingBox);
        transform.translate(Hand.getLocalTranslationFromJoint(childBoundingBox));
        return transform;
    }

    public LowerArmHandJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new LowerArmHandJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
