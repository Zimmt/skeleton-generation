package skeleton.elements.joints.arm;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class ElbowArmJoint extends ElbowJoint {
    private static float angle = (float) -Math.toRadians(90);

    public ElbowArmJoint(TerminalElement parent, Point3f position) {
        super(parent, position, angle, angle, ExtremityKind.ARM);
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
