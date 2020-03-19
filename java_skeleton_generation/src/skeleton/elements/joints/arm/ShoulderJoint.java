package skeleton.elements.joints.arm;

import skeleton.elements.ExtremityKind;
import skeleton.elements.joints.XZAngleBasedJoint;
import skeleton.elements.terminal.TerminalElement;
import skeleton.elements.terminal.UpperArm;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public abstract class ShoulderJoint extends XZAngleBasedJoint {

    private ExtremityKind extremityKind;
    boolean secondShoulder;

    public ShoulderJoint(TerminalElement parent, Point3f position,
                         float minFirstAngle, float maxFirstAngle, float minSecondAngle, float maxSecondAngle,
                         ExtremityKind extremityKind, boolean secondShoulder) {
        super(parent, position, minFirstAngle, maxFirstAngle, minSecondAngle, maxSecondAngle);
        this.extremityKind = extremityKind;
        this.secondShoulder = secondShoulder;
    }

    public ExtremityKind getExtremityKind() {
        return extremityKind;
    }

    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = super.calculateChildTransform(childBoundingBox);
        if (secondShoulder && extremityKind != ExtremityKind.WING) {
            transform.rotateAroundZ((float) Math.toRadians(80));
        }
        transform.translate(UpperArm.getLocalTranslationFromJoint(childBoundingBox));
        return transform;
    }

    public static ShoulderJoint newSpecificShoulderJoint(TerminalElement parent, Point3f position, ExtremityKind extremityKind, boolean secondShoulder) {
        switch (extremityKind) {
            case LEG:
            case FLOORED_LEG:
            case NON_FLOORED_LEG:
                return new ShoulderArmJoint(parent, position, extremityKind, secondShoulder);
            case FIN:
                return new ShoulderFinJoint(parent, position, secondShoulder);
            case WING:
                return new ShoulderWingJoint(parent, position, secondShoulder);
            default:
                return null;
        }
    }
}
