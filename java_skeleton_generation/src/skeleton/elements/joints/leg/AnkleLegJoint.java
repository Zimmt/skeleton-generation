package skeleton.elements.joints.leg;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class AnkleLegJoint extends AnkleJoint {

    private static float minAngleShin = (float) -Math.toRadians(170);
    private static float maxAngleShin = 0f;

    public AnkleLegJoint(TerminalElement parent, Point3f position, ExtremityKind extremityKind) {
        super(parent, position, minAngleShin, maxAngleShin, extremityKind);
        if (extremityKind != ExtremityKind.LEG && extremityKind != ExtremityKind.FLOORED_LEG && extremityKind != ExtremityKind.NON_FLOORED_LEG) {
            System.err.println("Invalid ankle leg joint kind");
        }
        setCurrentAngle(minAngleShin);
    }

    public AnkleLegJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new AnkleLegJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent), getExtremityKind());
    }
}
