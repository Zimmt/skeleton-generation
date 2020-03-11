package skeleton.elements.joints.arm;

import skeleton.elements.joints.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class ElbowFinJoint extends ElbowJoint {

    private static float angle = 0f;

    public ElbowFinJoint(TerminalElement parent, Point3f position) {
        super(parent, position, angle, angle, ExtremityKind.FIN);
        setCurrentAngle(angle);
    }

    @Override
    public boolean movementPossible(boolean nearerToFloor) {
        return false;
    }

    public ElbowFinJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new ElbowFinJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
