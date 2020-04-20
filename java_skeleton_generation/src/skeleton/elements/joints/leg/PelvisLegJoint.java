package skeleton.elements.joints.leg;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;
import skeleton.replacementRules.ExtremityPositioning;

import javax.vecmath.Point3f;

public class PelvisLegJoint extends PelvisJoint {

    private static float minFrontAnglePelvis = 0f;
    private static float maxFrontAnglePelvis = (float) Math.toRadians(45);
    private static float minSideAnglePelvis = (float) -Math.toRadians(170);
    private static float maxSideAnglePelvis = (float) Math.toRadians(170);

    public PelvisLegJoint(TerminalElement parent, Point3f position, ExtremityPositioning extremityPositioning) {
        super(parent, position, minFrontAnglePelvis, maxFrontAnglePelvis, minSideAnglePelvis, maxSideAnglePelvis, extremityPositioning);
        if (extremityPositioning.getExtremityKind() != ExtremityKind.LEG) {
            System.err.println("Invalid pelvic leg joint kind");
        }
        setCurrentFirstAngle(minFrontAnglePelvis);
        setCurrentSecondAngle(minSideAnglePelvis);
    }

    public PelvisLegJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new PelvisLegJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent), getExtremityPositioning());
    }
}
