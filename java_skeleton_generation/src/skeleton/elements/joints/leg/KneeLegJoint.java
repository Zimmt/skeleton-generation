package skeleton.elements.joints.leg;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class KneeLegJoint extends KneeJoint {

    private static float min = 0f;
    private static float max = (float) Math.toRadians(170);

    public KneeLegJoint(TerminalElement parent, Point3f position, ExtremityKind extremityKind) {
        super(parent, position, min, max, extremityKind);
        if (extremityKind != ExtremityKind.LEG && extremityKind != ExtremityKind.FLOORED_LEG && extremityKind != ExtremityKind.NON_FLOORED_LEG) {
            System.err.println("Invalid knee leg joint kind");
        }
        setCurrentAngle((float) Math.toRadians(160));
    }

    public KneeLegJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new KneeLegJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent), getExtremityKind());
    }
}