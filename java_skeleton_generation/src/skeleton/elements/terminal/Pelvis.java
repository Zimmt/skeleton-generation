package skeleton.elements.terminal;

import skeleton.SpinePart;
import skeleton.elements.ExtremityKind;
import skeleton.elements.joints.SpineOrientedJoint;
import skeleton.elements.joints.leg.PelvisJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Hüfte
 */
public class Pelvis extends TerminalElement {

    private final String kind = "pelvis";
    private SpineOrientedJoint tailJoint;
    private PelvisJoint firstJoint;
    private PelvisJoint secondJoint;

    public Pelvis(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor,
                  float tailJointSpinePosition, ExtremityKind[] extremityKinds) {
        super(transform, boundingBox, parent, ancestor);
        this.tailJoint = new SpineOrientedJoint(this, Pelvis.getTailJointPosition(boundingBox), SpinePart.TAIL, tailJointSpinePosition, parent.getGenerator());
        if (extremityKinds.length == 1) {
            this.firstJoint = PelvisJoint.newSpecificPelvicJoint(this, Pelvis.getOnlyLegJointPosition(boundingBox, extremityKinds[0]), extremityKinds[0]);
        } else if (extremityKinds.length == 2) {
            this.firstJoint = PelvisJoint.newSpecificPelvicJoint(this, Pelvis.getFirstLegJointPosition(boundingBox, extremityKinds[0]), extremityKinds[0]);
            this.secondJoint = PelvisJoint.newSpecificPelvicJoint(this, Pelvis.getSecondLegJointPosition(boundingBox, extremityKinds[1]), extremityKinds[1]);
        }
    }

    public String getKind() {
        return kind;
    }

    public SpineOrientedJoint getTailJoint() {
        return tailJoint;
    }

    public List<PelvisJoint> getLegJoints() {
        List<PelvisJoint> jointList = new ArrayList<>(2);
        if (firstJoint != null) jointList.add(firstJoint);
        if (secondJoint != null) jointList.add(secondJoint);
        return jointList;
    }

    public boolean canBeMirrored() { return false; }

    public Pelvis calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        System.out.println("Tried to mirror an element that should not be mirrored!");
        return null;
    }

    /**
     * @return the translation to move the joint between this element and its parent from this origin somewhere else.
     */
    public static Vector3f getLocalTranslationFromJoint(BoundingBox boundingBox) {
        return new Vector3f(0f, - 0.6f * boundingBox.getYLength(), -boundingBox.getZLength()/2f);
    }

    /**
     * @return the relative position for the joint between this element and the tail
     */
    private static Point3f getTailJointPosition(BoundingBox boundingBox) {
        return new Point3f(boundingBox.getXLength(), 0.7f * boundingBox.getYLength(), boundingBox.getZLength()/2f);
    }

    /**
     * @return the relative positions for the joints between this element and it's first child
     */
    private static Point3f getFirstLegJointPosition(BoundingBox childBoundingBox, ExtremityKind extremityKind) {
        return getLegJointPosition(childBoundingBox, extremityKind,1f/4f);
    }

    /**
     * @return the relative position for the joint between this element and it's second child
     */
    private static Point3f getSecondLegJointPosition(BoundingBox childBoundingBox, ExtremityKind extremityKind) {
        return getLegJointPosition(childBoundingBox, extremityKind,3f/4f);
    }

    /**
     * Only use this if the shoulder has only one child!
     * @return the relative position for the joint between this element and it's child
     */
    private static Point3f getOnlyLegJointPosition(BoundingBox childBoundingBox, ExtremityKind extremityKind) {
        return getLegJointPosition(childBoundingBox, extremityKind, 1f/2f);
    }

    private static Point3f getLegJointPosition(BoundingBox childBoundingBox, ExtremityKind extremityKind, float relativeXPosition) {
        return new Point3f(0.4f * childBoundingBox.getXLength(), 0.1f * childBoundingBox.getYLength(), 0.2f * childBoundingBox.getZLength());
    }
}
