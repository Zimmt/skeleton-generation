package skeleton.elements.joints.arm;

import skeleton.elements.joints.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class ShoulderArmJoint extends ShoulderJoint {

    private static float minFrontAngle = 0f;
    private static float maxFrontAngle = (float) Math.toRadians(170);
    private static float minSideAngle = 0f;
    private static float maxSideAngle = (float) Math.toRadians(170);

    public ShoulderArmJoint(TerminalElement parent, Point3f position, ExtremityKind extremityKind) {
        super(parent, position, minFrontAngle, maxFrontAngle, minSideAngle, maxSideAngle, extremityKind);
        if (extremityKind != ExtremityKind.LEG && extremityKind != ExtremityKind.FLOORED_LEG && extremityKind != ExtremityKind.NON_FLOORED_LEG) {
            System.err.println("Invalid shoulder arm joint kind");
        }
        setCurrentFirstAngle(minFrontAngle);
        setCurrentSecondAngle((float) Math.toRadians(90));
    }

    public ShoulderArmJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new ShoulderArmJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent), getExtremityKind());
    }
}
