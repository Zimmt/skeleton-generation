package skeleton.elements.joints.arm;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class ShoulderArmJoint extends ShoulderJoint {

    private static float minFrontAngle = 0f;
    private static float maxFrontAngle = (float) Math.toRadians(45);
    private static float minSideAngle = (float) -Math.toRadians(45); // needed for arms
    private static float maxSideAngle = (float) Math.toRadians(170);

    public ShoulderArmJoint(TerminalElement parent, Point3f position, ExtremityKind extremityKind, boolean secondShoulder) {
        super(parent, position, minFrontAngle, maxFrontAngle, minSideAngle, maxSideAngle, extremityKind, secondShoulder);
        if (extremityKind != ExtremityKind.LEG && extremityKind != ExtremityKind.ARM) {
            System.err.println("Invalid shoulder arm joint kind");
        }
        setCurrentFirstAngle(minFrontAngle);
        setCurrentSecondAngle(maxSideAngle);
    }

    public ShoulderArmJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new ShoulderArmJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent), getExtremityKind(), secondShoulder);
    }
}
