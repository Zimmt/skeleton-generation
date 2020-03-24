package skeleton.elements.joints.arm;

import skeleton.elements.ExtremityKind;
import skeleton.elements.terminal.TerminalElement;

import javax.vecmath.Point3f;

public class ElbowArmJoint extends ElbowJoint {

    private static float min = (float) -Math.toRadians(170);
    private static float max =  0f;

    public ElbowArmJoint(TerminalElement parent, Point3f position, ExtremityKind extremityKind) {
        super(parent, position, min, max, extremityKind);
        if (extremityKind != ExtremityKind.LEG && extremityKind != ExtremityKind.ARM) {
            System.err.println("Invalid elbow arm joint kind");
        }
        setCurrentAngle(min);
    }

    public ElbowArmJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new ElbowArmJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent), getExtremityKind());
    }
}
