package skeleton.elements.joints.leg;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class PelvicLegJoint extends PelvicJoint {

    private static float minFrontAnglePelvic = 0f;
    private static float maxFrontAnglePelvic = (float) Math.toRadians(170);
    private static float minSideAnglePelvic = (float) -Math.toRadians(170);
    private static float maxSideAnglePelvic = 0f;

    public PelvicLegJoint(TerminalElement parent, Point3f position, ExtremityKind extremityKind) {
        super(parent, position, minFrontAnglePelvic, maxFrontAnglePelvic, minSideAnglePelvic, maxSideAnglePelvic, extremityKind);
        if (extremityKind != ExtremityKind.LEG && extremityKind != ExtremityKind.FLOORED_LEG && extremityKind != ExtremityKind.NON_FLOORED_LEG) {
            System.err.println("Invalid pelvic leg joint kind");
        }
        setCurrentFirstAngle((float) Math.toRadians(90));
        setCurrentSecondAngle((float) -Math.toRadians(90));
    }

    public PelvicLegJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new PelvicLegJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent), getExtremityKind());
    }
}
