package skeleton.elements.joints.arm;

import skeleton.elements.joints.ExtremityKind;
import skeleton.elements.joints.OneAngleBasedJoint;
import skeleton.elements.terminal.Hand;
import skeleton.elements.terminal.TerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public abstract class WristJoint extends OneAngleBasedJoint {

    private ExtremityKind extremityKind;

    public WristJoint(TerminalElement parent, Point3f position, float minAngle, float maxAngle, ExtremityKind extremityKind) {
        super(parent, position, minAngle, maxAngle);
        this.extremityKind = extremityKind;
    }

    public ExtremityKind getExtremityKind() {
        return extremityKind;
    }

    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = super.calculateChildTransform(childBoundingBox);
        transform.translate(Hand.getLocalTranslationFromJoint(childBoundingBox));
        return transform;
    }

    public static WristJoint newSpecificWristJoint(TerminalElement parent, Point3f position, ExtremityKind extremityKind) {
        switch (extremityKind) {
            case LEG:
            case FLOORED_LEG:
            case NON_FLOORED_LEG:
                return new WristArmJoint(parent, position, extremityKind);
            case FIN:
                return new WristFinJoint(parent, position);
            case WING:
                return new WristWingJoint(parent, position);
            default:
                return null;
        }
    }
}
