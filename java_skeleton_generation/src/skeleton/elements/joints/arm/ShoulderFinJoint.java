package skeleton.elements.joints.arm;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class ShoulderFinJoint extends ShoulderJoint {

    private static float frontAngle = 0f;
    private static float minSideAngle = (float) Math.toRadians(45);
    private static float maxSideAngle = (float) Math.toRadians(225);

    public ShoulderFinJoint(TerminalElement parent, Point3f position, boolean secondShoulder) {
        super(parent, position, frontAngle, frontAngle, minSideAngle, maxSideAngle, ExtremityKind.FIN, secondShoulder);
        setCurrentFirstAngle(frontAngle);
        setCurrentSecondAngle(minSideAngle);
    }

    @Override
    public boolean movementPossible(boolean nearerToFloor, boolean second) {
        return false;
    }

    public ShoulderFinJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new ShoulderFinJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent), secondShoulder);
    }
}
