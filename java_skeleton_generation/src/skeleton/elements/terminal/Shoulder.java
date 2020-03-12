package skeleton.elements.terminal;

import skeleton.elements.joints.arm.ShoulderJoint;
import skeleton.elements.joints.ExtremityKind;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Optional;

public class Shoulder extends TerminalElement {

    private final String kind = "shoulder";
    private ShoulderJoint joint;

    public Shoulder(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor, boolean mirrored, ExtremityKind jointKind) {
        super(transform, boundingBox, parent, ancestor);
        this.joint = ShoulderJoint.newSpecificShoulderJoint(this, Shoulder.getJointPosition(boundingBox, mirrored), jointKind);
    }

    public String getKind() {
        return kind;
    }

    public ShoulderJoint getJoint() {
        return joint;
    }

    public boolean isMirrored() { return true; }

    public Shoulder calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        if (parent.isMirrored() && mirroredParent.isEmpty()) {
            System.err.println("Cannot mirror child when mirrored parent is not given!");
        }
        return new Shoulder(
                calculateMirroredTransform(parent),
                this.getBoundingBox().cloneBox(), // coordinate system is reflected so box must not be reflected!
                mirroredParent.orElse(parent), this.getAncestor(),
                true, joint.getExtremityKind());
    }

    /**
     * @return the translation to move the joint between this element and its parent from this origin somewhere else.
     */
    public static Vector3f getLocalTranslationFromJoint(BoundingBox boundingBox) {
        return new Vector3f(-boundingBox.getXLength()/2f, -boundingBox.getYLength()/2f, -boundingBox.getZLength());
    }

    /**
     * @return the relative position for the joint between this element and it's child
     */
    private static Point3f getJointPosition(BoundingBox boundingBox, boolean mirrored) {
        return new Point3f(boundingBox.getXLength()/2f, 0f, boundingBox.getZLength()/2f);
    }
}
