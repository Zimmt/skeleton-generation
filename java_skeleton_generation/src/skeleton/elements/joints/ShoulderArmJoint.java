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

    private static float minFrontAngleWing = (float) Math.toRadians(130);
    private static float minSideAngleWing = 0f;
    private static float maxSideAngleWing = (float) Math.toRadians(45);

    public ShoulderArmJoint(TerminalElement parent, Point3f position) {
        super(parent, position, 0f, maxFrontAngle, minSideAngle, maxSideAngle);
        currentSecondAngle = (float) Math.toRadians(90);
    }

    public void setRandomWingAngles() {
        currentFirstAngle = (random.nextFloat() * (maxFirstAngle - minFrontAngleWing)) + minFrontAngleWing;
        currentSecondAngle = (random.nextFloat() * (maxSideAngleWing - minSideAngleWing)) + minSideAngleWing;
        System.out.println("wing angles: " + Math.toDegrees(currentFirstAngle) + " , " + Math.toDegrees(currentSecondAngle));
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
