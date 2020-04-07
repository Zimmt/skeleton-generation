package skeleton.elements.joints.leg;

import skeleton.elements.joints.XZAngleBasedJoint;
import skeleton.elements.terminal.TerminalElement;
import skeleton.elements.terminal.Thigh;
import skeleton.replacementRules.ExtremityPositioning;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public abstract class PelvisJoint extends XZAngleBasedJoint {

    private ExtremityPositioning extremityPositioning;

    public PelvisJoint(TerminalElement parent, Point3f position, float minFirstAngle, float maxFirstAngle, float minSecondAngle, float maxSecondAngle,
                       ExtremityPositioning extremityPositioning) {
        super(parent, position, minFirstAngle, maxFirstAngle, minSecondAngle, maxSecondAngle);
        this.extremityPositioning = extremityPositioning;
    }

    public ExtremityPositioning getExtremityPositioning() {
        return extremityPositioning;
    }

    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = super.calculateChildTransform(childBoundingBox);
        transform.translate(Thigh.getLocalTranslationFromJoint(childBoundingBox));
        return transform;
    }

    protected boolean getTurnDirectionNearerToFloorForPositiveVerticalPosition() {
        return true;
    }

    public static PelvisJoint newSpecificPelvisJoint(TerminalElement parent, Point3f position, ExtremityPositioning extremityPositioning) {
        switch (extremityPositioning.getExtremityKind()) {
            case LEG:
            case ARM:
                return new PelvisLegJoint(parent, position, extremityPositioning);
            case FIN:
                return new PelvisFinJoint(parent, position, extremityPositioning);
            case WING:
                System.err.println("No wings for pelvic possible");
            default:
                return null;
        }
    }
}
