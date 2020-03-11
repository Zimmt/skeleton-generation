package skeleton.elements.joints.leg;

import skeleton.elements.joints.ExtremityKind;
import skeleton.elements.joints.XZAngleBasedJoint;
import skeleton.elements.terminal.TerminalElement;
import skeleton.elements.terminal.Thigh;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public abstract class PelvicJoint extends XZAngleBasedJoint {

    private ExtremityKind extremityKind;

    public PelvicJoint(TerminalElement parent, Point3f position, float minFirstAngle, float maxFirstAngle, float minSecondAngle, float maxSecondAngle, ExtremityKind extremityKind) {
        super(parent, position, minFirstAngle, maxFirstAngle, minSecondAngle, maxSecondAngle);
        this.extremityKind = extremityKind;
    }

    public ExtremityKind getExtremityKind() {
        return extremityKind;
    }

    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = super.calculateChildTransform(childBoundingBox);
        transform.translate(Thigh.getLocalTranslationFromJoint(childBoundingBox));
        return transform;
    }

    public static PelvicJoint newSpecificPelvicJoint(TerminalElement parent, Point3f position, ExtremityKind extremityKind) {
        switch (extremityKind) {
            case LEG:
            case FLOORED_LEG:
            case NON_FLOORED_LEG:
                return new PelvicLegJoint(parent, position, extremityKind);
            case FIN:
                return new PelvicFinJoint(parent, position);
            case WING:
                System.err.println("No wings for pelvic possible");
            default:
                return null;
        }
    }
}
