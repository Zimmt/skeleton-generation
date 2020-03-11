package skeleton.elements.joints.leg;

import skeleton.elements.joints.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class AnkleFinJoint extends AnkleJoint {

    private static float angle = 0f;

    public AnkleFinJoint(TerminalElement parent, Point3f position) {
        super(parent, position, angle, angle, ExtremityKind.FIN);
        setCurrentAngle(angle);
    }

    @Override
    public boolean movementPossible(boolean nearerToFloor) {
        return false;
    }

    public AnkleFinJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new AnkleFinJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
