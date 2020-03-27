package skeleton.elements.joints.leg;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class PelvisFinJoint extends PelvisJoint {

    private static float frontAngle = 0f;
    private static float minSideAngle = (float) Math.toRadians(45);
    private static float maxSideAngle = (float) Math.toRadians(225);

    public PelvisFinJoint(TerminalElement parent, Point3f position) {
        super(parent, position, frontAngle, frontAngle, minSideAngle, maxSideAngle, ExtremityKind.FIN);
        setCurrentFirstAngle(frontAngle);
        setCurrentSecondAngle(minSideAngle);
    }

    @Override
    public boolean movementPossible(boolean nearerToFloor, boolean second) {
        return false;
    }

    public PelvisFinJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new PelvisFinJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
