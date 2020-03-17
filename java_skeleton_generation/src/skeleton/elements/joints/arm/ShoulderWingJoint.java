package skeleton.elements.joints.arm;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class ShoulderWingJoint extends ShoulderJoint {

    private static float minFrontAngle = (float) Math.toRadians(130);
    private static float maxFrontAngle = (float) Math.toRadians(170);
    private static float minSideAngle = 0f;
    private static float maxSideAngle = (float) Math.toRadians(45);

    public ShoulderWingJoint(TerminalElement parent, Point3f position) {
        super(parent, position, minFrontAngle, maxFrontAngle, minSideAngle, maxSideAngle, ExtremityKind.WING);
        setCurrentFirstAngle(minFrontAngle);
        setCurrentSecondAngle(minSideAngle);
    }

    public ShoulderWingJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new ShoulderWingJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
