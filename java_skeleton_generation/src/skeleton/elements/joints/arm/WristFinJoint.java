package skeleton.elements.joints.arm;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class WristFinJoint extends WristJoint {

    private static float angle = 0f;

    public WristFinJoint(TerminalElement parent, Point3f position) {
        super(parent, position, angle, angle, ExtremityKind.FIN);
        setCurrentAngle(angle);
    }

    @Override
    public boolean movementPossible(boolean nearerToFloor) {
        return false;
    }

    public WristFinJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new WristFinJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
