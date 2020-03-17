package skeleton.elements.joints.arm;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class ElbowWingJoint extends ElbowJoint {

    private static float min = (float) -Math.toRadians(45);
    private static float max =  0f;

    public ElbowWingJoint(TerminalElement parent, Point3f position) {
        super(parent, position, min, max, ExtremityKind.WING);
        setCurrentAngle(max);
    }

    public ElbowWingJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new ElbowWingJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
