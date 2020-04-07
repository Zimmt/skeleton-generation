package skeleton.elements.joints.arm;

import skeleton.elements.joints.XZAngleBasedJoint;
import skeleton.elements.terminal.TerminalElement;
import skeleton.elements.terminal.UpperArm;
import skeleton.replacementRules.ExtremityPositioning;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public abstract class ShoulderJoint extends XZAngleBasedJoint {

    private ExtremityPositioning extremityPositioning;
    boolean secondShoulder;

    public ShoulderJoint(TerminalElement parent, Point3f position,
                         float minFirstAngle, float maxFirstAngle, float minSecondAngle, float maxSecondAngle,
                         ExtremityPositioning extremityPositioning, boolean secondShoulder) {
        super(parent, position, minFirstAngle, maxFirstAngle, minSecondAngle, maxSecondAngle);
        this.extremityPositioning = extremityPositioning;
        this.secondShoulder = secondShoulder;
    }

    public ExtremityPositioning getExtremityPositioning() {
        return extremityPositioning;
    }

    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = super.calculateChildTransform(childBoundingBox);
        transform.translate(UpperArm.getLocalTranslationFromJoint(childBoundingBox));
        return transform;
    }

    protected boolean getTurnDirectionNearerToFloorForPositiveVerticalPosition() {
        return false;
    }

    public static ShoulderJoint newSpecificShoulderJoint(TerminalElement parent, Point3f position, ExtremityPositioning extremityPositioning, boolean secondShoulder) {
        switch (extremityPositioning.getExtremityKind()) {
            case LEG:
                return new ShoulderLegJoint(parent, position, secondShoulder, extremityPositioning);
            case ARM:
                return new ShoulderArmJoint(parent, position, secondShoulder, extremityPositioning);
            case FIN:
                return new ShoulderFinJoint(parent, position, secondShoulder, extremityPositioning);
            case WING:
                return new ShoulderWingJoint(parent, position, secondShoulder, extremityPositioning);
            default:
                return null;
        }
    }
}
