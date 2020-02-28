package skeleton.elements.terminal;

import skeleton.SpinePart;
import skeleton.elements.joints.PelvicThighJoint;
import skeleton.elements.joints.SpineOrientedJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Optional;

/**
 * Hüfte
 */
public class Pelvic extends TerminalElement {

    private final String kind = "pelvic";
    private SpineOrientedJoint tailJoint;
    private PelvicThighJoint legJoint;

    public Pelvic(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor, float tailJointSpinePosition) {
        super(transform, boundingBox, parent, ancestor);
        this.tailJoint = new SpineOrientedJoint(this, Pelvic.getTailJointPosition(boundingBox), SpinePart.TAIL, tailJointSpinePosition, parent.getGenerator());
        this.legJoint = new PelvicThighJoint(this, Pelvic.getLegJointPosition(boundingBox));
    }

    public String getKind() {
        return kind;
    }

    public SpineOrientedJoint getTailJoint() {
        return tailJoint;
    }

    public PelvicThighJoint getLegJoint() {
        return legJoint;
    }

    public boolean isMirrored() { return false; }

    public Pelvic calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        System.out.println("Tried to mirror an element that should not be mirrored!");
        return null;
    }

    /**
     * @return the translation to move the joint between this element and its parent from this origin somewhere else.
     */
    public static Vector3f getLocalTranslationFromJoint(BoundingBox boundingBox) {
        return new Vector3f(0f, -boundingBox.getYLength()/2f, -boundingBox.getZLength()/2f);
    }

    /**
     * @return the relative position for the joint between this element and the tail
     */
    private static Point3f getTailJointPosition(BoundingBox boundingBox) {
        return new Point3f(boundingBox.getXLength(), boundingBox.getYLength()/2f, boundingBox.getZLength()/2f);
    }

    /**
     * @return the relative position for the joint between this element and the leg
     */
    private static Point3f getLegJointPosition(BoundingBox boundingBox) {
        return new Point3f(boundingBox.getXLength()/2f, 0f, boundingBox.getZLength()/4f);
    }
}
