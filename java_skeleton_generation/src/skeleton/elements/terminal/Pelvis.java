package skeleton.elements.terminal;

import skeleton.elements.joints.leg.PelvisJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import skeleton.replacementRules.ExtremityPositioning;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * scaling:
 * x: same as vertebra x
 * y: same as x
 * z: distance between leg joints
 */
public class Pelvis extends TerminalElement {

    private final String kind = "pelvis";
    private PelvisJoint firstJoint;
    private PelvisJoint secondJoint;

    public Pelvis(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor, ExtremityPositioning[] extremityPositionings) {
        super(transform, boundingBox, parent, ancestor);
        if (extremityPositionings.length == 1) {
            this.firstJoint = PelvisJoint.newSpecificPelvisJoint(this, Pelvis.getOnlyLegJointPosition(boundingBox), extremityPositionings[0]);
        } else if (extremityPositionings.length == 2) {
            this.firstJoint = PelvisJoint.newSpecificPelvisJoint(this, Pelvis.getFirstLegJointPosition(boundingBox), extremityPositionings[0]);
            this.secondJoint = PelvisJoint.newSpecificPelvisJoint(this, Pelvis.getSecondLegJointPosition(boundingBox), extremityPositionings[1]);
        }
    }

    public String getKind() {
        if (secondJoint != null) {
            return kind + "_double_joint";
        } else {
            return kind;
        }
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
        return new Vector3f(-boundingBox.getXLength()/2f, -boundingBox.getYLength(), -boundingBox.getZLength()/2f);
    }

    /**
     * @return the relative positions for the joints between this element and it's first child
     */
    private static Point3f getFirstLegJointPosition(BoundingBox childBoundingBox) {
        return getOnlyLegJointPosition(childBoundingBox);
    }

    /**
     * @return the relative position for the joint between this element and it's second child
     */
    private static Point3f getSecondLegJointPosition(BoundingBox childBoundingBox) {
        return getLegJointPosition(childBoundingBox, 4f);
    }

    /**
     * Only use this if the shoulder has only one child!
     * @return the relative position for the joint between this element and it's child
     */
    private static Point3f getOnlyLegJointPosition(BoundingBox childBoundingBox) {
        return getLegJointPosition(childBoundingBox, 2.5f);
    }

    private static Point3f getLegJointPosition(BoundingBox childBoundingBox, float relativeXPos) {
        return new Point3f(relativeXPos * childBoundingBox.getXLength(), -0.2f * childBoundingBox.getYLength(), 0f);
    }
}
