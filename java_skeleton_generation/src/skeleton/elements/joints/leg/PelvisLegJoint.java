package skeleton.elements.joints.leg;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class PelvisLegJoint extends PelvisJoint {

    private static float minFrontAnglePelvic = 0f;
    private static float maxFrontAnglePelvic = (float) Math.toRadians(45);
    private static float minSideAnglePelvic = (float) -Math.toRadians(170);
    private static float maxSideAnglePelvic = 0f;

    public PelvisLegJoint(TerminalElement parent, Point3f position, ExtremityKind extremityKind) {
        super(parent, position, minFrontAnglePelvic, maxFrontAnglePelvic, minSideAnglePelvic, maxSideAnglePelvic, extremityKind);
        if (extremityKind != ExtremityKind.LEG && extremityKind != ExtremityKind.ARM) {
            System.err.println("Invalid pelvic leg joint kind");
        }
        setCurrentFirstAngle(minFrontAnglePelvic);
        setCurrentSecondAngle(minSideAnglePelvic);
    }

    public PelvisLegJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new PelvisLegJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent), getExtremityKind());
    }
}
