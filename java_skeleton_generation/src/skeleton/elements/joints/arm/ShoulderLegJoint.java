package skeleton.elements.joints.arm;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class ShoulderLegJoint extends ShoulderJoint {

    private static float minFrontAngle = 0f;
    private static float maxFrontAngle = (float) Math.toRadians(45);
    private static float minSideAngle =  0f;
    private static float maxSideAngle = (float) Math.toRadians(170);

    public ShoulderLegJoint(TerminalElement parent, Point3f position, boolean secondShoulder) {
        super(parent, position, minFrontAngle, maxFrontAngle, minSideAngle, maxSideAngle, ExtremityKind.LEG, secondShoulder);
        setCurrentFirstAngle(minFrontAngle);
        setCurrentSecondAngle(maxSideAngle);
    }

    public ShoulderLegJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new ShoulderLegJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent), secondShoulder);
    }
}
