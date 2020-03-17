package skeleton.elements.joints.arm;

import skeleton.elements.ExtremityKind;
import skeleton.elements.joints.OneAngleBasedJoint;
import skeleton.elements.terminal.LowerArm;
import skeleton.elements.terminal.TerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public abstract class ElbowJoint extends OneAngleBasedJoint {

    private ExtremityKind extremityKind;

    public ElbowJoint(TerminalElement parent, Point3f position, float minAngle, float maxAngle, ExtremityKind extremityKind) {
        super(parent, position, minAngle, maxAngle);
        this.extremityKind = extremityKind;
    }

    public ExtremityKind getExtremityKind() {
        return extremityKind;
    }

    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = super.calculateChildTransform(childBoundingBox);
        transform.translate(LowerArm.getLocalTranslationFromJoint(childBoundingBox));
        return transform;
    }

    public static ElbowJoint newSpecificElbowJoint(TerminalElement parent, Point3f position, ExtremityKind extremityKind) {
        switch (extremityKind) {
            case LEG:
            case FLOORED_LEG:
            case NON_FLOORED_LEG:
                return new ElbowArmJoint(parent, position, extremityKind);
            case FIN:
                return new ElbowFinJoint(parent, position);
            case WING:
                return new ElbowWingJoint(parent, position);
            default:
                return null;
        }
    }
}
