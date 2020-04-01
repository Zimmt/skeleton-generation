package skeleton.elements.joints.arm;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class WristArmJoint extends WristJoint {

    private static float min = (float) -Math.toRadians(170);
    private static float max = 0f;

    public WristArmJoint(TerminalElement parent, Point3f position) {
        super(parent, position, min, max, ExtremityKind.LEG);
        setCurrentAngle(min);
    }

    public WristArmJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new WristArmJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
