package skeleton.elements.joints.arm;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class WristFinOrArmJoint extends WristJoint {

    private static float angle = 0f;

    public WristFinOrArmJoint(TerminalElement parent, Point3f position, ExtremityKind extremityKind) {
        super(parent, position, angle, angle, extremityKind);
        setCurrentAngle(angle);
    }

    @Override
    public boolean movementPossible(boolean nearerToFloor) {
        return false;
    }

    public WristFinOrArmJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new WristFinOrArmJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent), getExtremityKind());
    }
}
