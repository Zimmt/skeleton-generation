package skeleton.elements.joints.arm;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class ShoulderFinJoint extends ShoulderJoint {

    private static float frontAngle = 0f;
    // side angle is determined by orientation to world x axis, so range is not important
    private static float minSideAngle = 0f;
    private static float maxSideAngle = (float) Math.toRadians(360);

    public ShoulderFinJoint(TerminalElement parent, Point3f position, boolean secondShoulder) {
        super(parent, position, frontAngle, frontAngle, minSideAngle, maxSideAngle, ExtremityKind.FIN, secondShoulder);

        setCurrentFirstAngle(frontAngle);
        Vector3f localY = new Vector3f(0f, -1f, 0f);
        parent.calculateWorldTransform().applyOnVector(localY);
        Vector3f worldX = new Vector3f(1f, 0f, 0f);
        float sideAngle = worldX.angle(localY); // turn direction is always positive
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
