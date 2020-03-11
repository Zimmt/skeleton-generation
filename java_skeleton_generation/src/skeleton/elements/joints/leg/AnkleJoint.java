package skeleton.elements.joints.leg;

import skeleton.elements.joints.ExtremityKind;
import skeleton.elements.joints.OneAngleBasedJoint;
import skeleton.elements.terminal.Foot;
import skeleton.elements.terminal.TerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public abstract class AnkleJoint extends OneAngleBasedJoint {

    private ExtremityKind extremityKind;

    public AnkleJoint(TerminalElement parent, Point3f position, float minAngle, float maxAngle, ExtremityKind extremityKind) {
        super(parent, position, minAngle, maxAngle);
        this.extremityKind = extremityKind;
    }

    public ExtremityKind getExtremityKind() {
        return extremityKind;
    }

    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = super.calculateChildTransform(childBoundingBox);
        transform.translate(Foot.getLocalTranslationFromJoint(childBoundingBox));
        return transform;
    }

    public static AnkleJoint newSpecificAnkleJoint(TerminalElement parent, Point3f position, ExtremityKind extremityKind) {
        switch (extremityKind) {
            case LEG:
            case FLOORED_LEG:
            case NON_FLOORED_LEG:
                return new AnkleLegJoint(parent, position, extremityKind);
            case FIN:
                return new AnkleFinJoint(parent, position);
            case WING:
                System.err.println("no wings allowed here");
            default:
                return null;
        }
    }
}
