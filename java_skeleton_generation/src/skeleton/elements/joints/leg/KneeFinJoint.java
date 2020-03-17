package skeleton.elements.joints.leg;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class KneeFinJoint extends KneeJoint {

    private static float angle = 0f;

    public KneeFinJoint(TerminalElement parent, Point3f position) {
        super(parent, position, angle, angle, ExtremityKind.FIN);
        setCurrentAngle(angle);
    }

    @Override
    public boolean movementPossible(boolean nearerToFloor) {
        return false;
    }

    public KneeFinJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new KneeFinJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
