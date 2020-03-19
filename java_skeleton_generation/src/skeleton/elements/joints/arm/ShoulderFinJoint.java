package skeleton.elements.joints.arm;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class ShoulderFinJoint extends ShoulderJoint {

    private static float frontAngle = (float) Math.toRadians(90);
    private static float sideAngle = (float) Math.toRadians(90);

    public ShoulderFinJoint(TerminalElement parent, Point3f position, boolean secondShoulder) {
        super(parent, position, frontAngle, frontAngle, sideAngle, sideAngle, ExtremityKind.FIN, secondShoulder);
        setCurrentFirstAngle(frontAngle);
        setCurrentSecondAngle(sideAngle);
    }

    @Override
    public boolean movementPossible(boolean nearerToFloor, boolean second) {
        return false;
    }

    public ShoulderFinJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new ShoulderFinJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent), secondShoulder);
    }
}
