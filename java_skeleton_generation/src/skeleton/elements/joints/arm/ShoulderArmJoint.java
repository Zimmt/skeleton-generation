package skeleton.elements.joints.arm;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;
import skeleton.replacementRules.ExtremityPositioning;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class ShoulderArmJoint extends ShoulderJoint {
    private static float frontAngle = 0f;
    // side angle is determined by orientation to world x axis, so range is not important
    private static float minSideAngle = 0f;
    private static float maxSideAngle = (float) Math.toRadians(360);

    public ShoulderArmJoint(TerminalElement parent, Point3f position, boolean secondShoulder, ExtremityPositioning extremityPositioning) {
        super(parent, position, frontAngle, frontAngle, minSideAngle, maxSideAngle, extremityPositioning, secondShoulder);

        setCurrentFirstAngle(frontAngle);
        Vector3f localY = new Vector3f(0f, -1f, 0f);
        parent.calculateWorldTransform().applyOnVector(localY);
        Vector3f worldY = new Vector3f(0f, -1f, 0f);
        float sideAngle = worldY.angle(localY); // turn direction is (most probably) always positive (and if not the angle is small)
        setCurrentSecondAngle(sideAngle);
    }

    @Override
    public boolean movementPossible(boolean nearerToFloor, boolean second) {
        return false;
    }

    public ShoulderFinJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new ShoulderFinJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent), secondShoulder, new ExtremityPositioning(ExtremityKind.ARM));
    }
}
