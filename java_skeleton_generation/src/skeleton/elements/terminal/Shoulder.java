package skeleton.elements.terminal;

import skeleton.elements.ExtremityKind;
import skeleton.elements.joints.arm.ShoulderJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.*;

public class Shoulder extends TerminalElement {

    private final String kind = "shoulder";
    private ShoulderJoint firstJoint;
    private ShoulderJoint secondJoint;
    private boolean secondShoulder;

    public Shoulder(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor,
                    ExtremityKind[] extremityKinds, boolean secondShoulder) {
        super(transform, boundingBox, parent, ancestor);
        if (extremityKinds.length == 1) {
            this.firstJoint = ShoulderJoint.newSpecificShoulderJoint(this,
                    Shoulder.getOnlyJointPosition(boundingBox, extremityKinds[0], secondShoulder), extremityKinds[0], secondShoulder);
        } else if (extremityKinds.length == 2) {
            this.firstJoint = ShoulderJoint.newSpecificShoulderJoint(this,
                    Shoulder.getFirstJointPosition(boundingBox, extremityKinds[0], secondShoulder), extremityKinds[0], secondShoulder);
            this.secondJoint = ShoulderJoint.newSpecificShoulderJoint(this,
                    Shoulder.getSecondJointPosition(boundingBox, extremityKinds[1], secondShoulder), extremityKinds[1], secondShoulder);
        }
        this.secondShoulder = secondShoulder;
    }

    private Shoulder(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor,
                     boolean secondShoulder, boolean isMirroredVersion) {
        super(transform, boundingBox, parent, ancestor);
        super.isMirroredVersion = isMirroredVersion;
        this.secondShoulder = secondShoulder;
    }

    public String getKind() {
        return kind;
    }

    public List<ShoulderJoint> getJoints() {
        List<ShoulderJoint> jointList = new ArrayList<>(2);
        if (firstJoint != null) jointList.add(firstJoint);
        if (secondJoint != null) jointList.add(secondJoint);
        return jointList;
    }

    public boolean canBeMirrored() { return true; }

    public Shoulder calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        if (parent.canBeMirrored() && mirroredParent.isEmpty()) {
            System.err.println("Cannot mirror child when mirrored parent is not given!");
        }
        return new Shoulder(
                calculateMirroredTransform(parent),
                this.getBoundingBox().cloneBox(), // coordinate system is reflected so box must not be reflected!
                mirroredParent.orElse(parent), this.getAncestor(), secondShoulder, true);
    }

    /**
     * @return the translation to move the joint between this element and its parent from this origin somewhere else.
     */
    public static Vector3f getLocalTranslationFromJoint(BoundingBox boundingBox) {
        return new Vector3f(-0.4f * boundingBox.getXLength(), 0f, 0f);
    }

    /**
     * @return the relative positions for the joints between this element and it's first child
     */
    private static Point3f getFirstJointPosition(BoundingBox childBoundingBox, ExtremityKind extremityKind, boolean secondShoulder) {
        return getJointPosition(childBoundingBox, extremityKind, secondShoulder,1f/4f);
    }

    /**
     * @return the relative position for the joint between this element and it's second child
     */
    private static Point3f getSecondJointPosition(BoundingBox childBoundingBox, ExtremityKind extremityKind, boolean secondShoulder) {
        return getJointPosition(childBoundingBox, extremityKind, secondShoulder,3f/4f);
    }

    /**
     * Only use this if the shoulder has only one child!
     * @return the relative position for the joint between this element and it's child
     */
    private static Point3f getOnlyJointPosition(BoundingBox childBoundingBox, ExtremityKind extremityKind, boolean secondShoulder) {
        return getJointPosition(childBoundingBox, extremityKind, secondShoulder, 1f/2f);
    }

    private static Point3f getJointPosition(BoundingBox childBoundingBox, ExtremityKind extremityKind, boolean secondShoulder, float relativeXPosition) {
        return new Point3f(0.2f * childBoundingBox.getXLength(), 0.4f * childBoundingBox.getYLength(), 0.8f * childBoundingBox.getZLength());
    }
}
