package skeleton.elements.joints.arm;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class WristWingJoint extends WristJoint {

    private static float min = 0f;
    private static float max = (float) Math.toRadians(90);

    public WristWingJoint(TerminalElement parent, Point3f position) {
        super(parent, position, min, max, ExtremityKind.WING);
        setCurrentAngle(min);
    }

    public WristWingJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new WristWingJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
