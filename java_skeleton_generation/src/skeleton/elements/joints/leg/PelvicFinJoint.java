package skeleton.elements.joints.leg;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class PelvicFinJoint extends PelvicJoint {

    private static float frontAngle = 0f;
    private static float minSideAngle = (float) Math.toRadians(45);
    private static float maxSideAngle = (float) Math.toRadians(225);

    public PelvicFinJoint(TerminalElement parent, Point3f position) {
        super(parent, position, frontAngle, frontAngle, minSideAngle, maxSideAngle, ExtremityKind.FIN);
        setCurrentFirstAngle(frontAngle);
        setCurrentSecondAngle(minSideAngle);
    }

    @Override
    public boolean movementPossible(boolean nearerToFloor, boolean second) {
        return false;
    }

    public PelvicFinJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new PelvicFinJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
