package skeleton.elements.joints.arm;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class ShoulderArmJoint extends ShoulderJoint {

    private static float minFrontAngle = 0f;
    private static float maxFrontAngle = (float) Math.toRadians(170);
    private static float minSideAngle = (float) -Math.toRadians(90);
    private static float maxSideAngle = (float) Math.toRadians(170);

    public ShoulderArmJoint(TerminalElement parent, Point3f position, ExtremityKind extremityKind, boolean secondShoulder) {
        super(parent, position, minFrontAngle, maxFrontAngle, minSideAngle, maxSideAngle, extremityKind, secondShoulder);
        if (extremityKind != ExtremityKind.LEG && extremityKind != ExtremityKind.FLOORED_LEG && extremityKind != ExtremityKind.NON_FLOORED_LEG) {
            System.err.println("Invalid shoulder arm joint kind");
        }
        setCurrentFirstAngle((float) Math.toRadians(90));
        setCurrentSecondAngle((float) Math.toRadians(90));
    }

    public ShoulderArmJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new ShoulderArmJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent), getExtremityKind(), secondShoulder);
    }
}
