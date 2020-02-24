package skeleton.elements.terminal;

import skeleton.elements.joints.SpineOrientedJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Optional;

/**
 * Wirbel
 */
public class Vertebra extends TerminalElement {

    private final String kind = "vertebra";
    SpineOrientedJoint joint;

    public Vertebra(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor, SpineOrientedJoint joint) {
        super(transform, boundingBox, parent, ancestor);
        this.joint = joint;
    }

    public String getKind() {
        return kind;
    }

    public SpineOrientedJoint getJoint() {
        return joint;
    }

    public boolean isMirrored() { return false; }

    public Vertebra calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        System.out.println("Tried to mirror an element that should not be mirrored!");
        return null;
    }

    /**
     * @return the translation to move the joint between this element and its parent from this origin somewhere else.
     */
    public static Vector3f getLocalTranslationFromJoint(BoundingBox boundingBox) {
        return new Vector3f(0f, -boundingBox.getYLength() / 2f, -boundingBox.getZLength() / 2f);
    }

    /**
     * @param positiveXDir if the joint is in positive x direction (needed for vertebrae that are spawned in different directions from root)
     * @return the relative position for the joint between this element and it's child
     */
    public static Point3f getJointPosition(BoundingBox boundingBox, boolean positiveXDir) {
        return new Point3f(positiveXDir ? boundingBox.getXLength() : 0f, boundingBox.getYLength()/2f, boundingBox.getZLength()/2f);
    }
}
