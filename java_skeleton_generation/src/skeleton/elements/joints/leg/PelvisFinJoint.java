package skeleton.elements.joints.leg;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;
import skeleton.replacementRules.ExtremityPositioning;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class PelvisFinJoint extends PelvisJoint {

    private static float frontAngle = 0f;
    // side angle is determined by orientation to world x axis, so range is not important
    private static float minSideAngle = 0f;
    private static float maxSideAngle = (float) Math.toRadians(360);

    public PelvisFinJoint(TerminalElement parent, Point3f position, ExtremityPositioning extremityPositioning) {
        super(parent, position, frontAngle, frontAngle, minSideAngle, maxSideAngle, extremityPositioning);

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

    public PelvisFinJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new PelvisFinJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent), new ExtremityPositioning(ExtremityKind.FIN));
    }
}
