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

    public ShoulderJoint(TerminalElement parent, Point3f position,
                         float minFirstAngle, float maxFirstAngle, float minSecondAngle, float maxSecondAngle,
                         ExtremityKind extremityKind) {
        super(parent, position, minFirstAngle, maxFirstAngle, minSecondAngle, maxSecondAngle);
        this.extremityKind = extremityKind;
    }

    public ExtremityKind getExtremityKind() {
        return extremityKind;
    }

    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = super.calculateChildTransform(childBoundingBox);
        transform.translate(UpperArm.getLocalTranslationFromJoint(childBoundingBox));
        return transform;
    }

    public static ShoulderJoint newSpecificShoulderJoint(TerminalElement parent, Point3f position, ExtremityKind extremityKind) {
        switch (extremityKind) {
            case LEG:
            case FLOORED_LEG:
            case NON_FLOORED_LEG:
                return new ShoulderArmJoint(parent, position, extremityKind);
            case FIN:
                return new ShoulderFinJoint(parent, position);
            case WING:
                return new ShoulderWingJoint(parent, position);
            default:
                return null;
        }
    }
}
