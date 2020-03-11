package skeleton.elements.joints.leg;

import skeleton.elements.joints.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class PelvicFinJoint extends PelvicJoint {

    private static float sideAngle = (float) Math.toRadians(90);
    private static float frontAngle = (float) Math.toRadians(90);

    public PelvicFinJoint(TerminalElement parent, Point3f position) {
        super(parent, position, sideAngle, sideAngle, frontAngle, frontAngle, ExtremityKind.FIN);
        setCurrentFirstAngle(sideAngle);
        setCurrentSecondAngle(frontAngle);
    }

    @Override
    public boolean movementPossible(boolean nearerToFloor, boolean side) {
        return false;
    }

    public PelvicFinJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new PelvicFinJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
