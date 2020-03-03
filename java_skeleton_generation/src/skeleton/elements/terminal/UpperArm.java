package skeleton.elements.terminal;

import skeleton.elements.joints.UpperLowerArmJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Optional;

public class UpperArm extends TerminalElement {

    private final String kind = "upper arm";
    UpperLowerArmJoint joint;

    public UpperArm(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor, boolean mirrored) {
        super(transform, boundingBox, parent, ancestor);
        this.joint = new UpperLowerArmJoint(this, UpperArm.getJointPosition(boundingBox, mirrored));
    }

    public String getKind() {
        return kind;
    }

    public UpperLowerArmJoint getJoint() {
        return joint;
    }

    public boolean isMirrored() { return true; }

    public UpperArm calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        if (parent.isMirrored() && mirroredParent.isEmpty()) {
            System.err.println("Cannot mirror child when mirrored parent is not given!");
        }
        return new UpperArm(
                calculateMirroredTransform(parent),
                this.getBoundingBox().cloneBox(), // coordinate system is reflected so box must not be reflected!
                mirroredParent.orElse(parent), this.getAncestor(),
                true);
    }

    /**
     * @return the translation to move the joint between this element and its parent from this origin somewhere else.
     */
    public static Vector3f getLocalTranslationFromJoint(BoundingBox boundingBox) {
        return new Vector3f(-boundingBox.getXLength()/2f, -boundingBox.getYLength(), -boundingBox.getZLength()/2f);
    }

    /**
     * @return the relative position for the joint between this element and it's child
     */
    public static Point3f getJointPosition(BoundingBox boundingBox, boolean mirrored) {
        return new Point3f(boundingBox.getXLength()/2f,0f, boundingBox.getZLength()/2f);
    }
}
