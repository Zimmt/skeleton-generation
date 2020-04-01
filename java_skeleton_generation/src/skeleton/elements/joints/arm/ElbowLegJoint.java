package skeleton.elements.joints.arm;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class ElbowLegJoint extends ElbowJoint {

    private static float min = (float) -Math.toRadians(170);
    private static float max =  0f;

    public ElbowLegJoint(TerminalElement parent, Point3f position) {
        super(parent, position, min, max, ExtremityKind.LEG);
        setCurrentAngle(min);
    }

    public ElbowLegJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new ElbowLegJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
