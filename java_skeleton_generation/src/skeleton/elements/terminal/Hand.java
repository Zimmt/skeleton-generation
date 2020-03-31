package skeleton.elements.terminal;

import skeleton.elements.ExtremityKind;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Vector3f;
import java.util.Optional;

public class Hand extends TerminalElement {

    private final String kind = "hand";

    public Hand(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor) {
        super(transform, boundingBox, parent, ancestor);
    }

    private Hand(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor, boolean isMirroredVersion) {
        super(transform, boundingBox, parent, ancestor);
        super.isMirroredVersion = isMirroredVersion;
    }

    public String getKind() {
        if (getParent().getJoint().getExtremityKind() == ExtremityKind.WING) {
            return "wing_hand";
        } else {
            Vector3f localY = new Vector3f(0f, 1f, 0f);
            calculateWorldTransform().applyOnVector(localY);
            localY.z = 0f;
            float angle1 = localY.angle(new Vector3f(1f, 0f, 0f));
            float angle2 = localY.angle(new Vector3f(-1f, 0f, 0f));
            if (angle1 < Math.toRadians(45) || angle2 < Math.toRadians(45)) {
                return kind;
            } else {
                return "hoof";
            }
        }
    }

    public boolean canBeMirrored() { return true; }

    public Hand calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        if (parent.canBeMirrored() && mirroredParent.isEmpty()) {
            System.err.println("Cannot mirror child when mirrored parent is not given!");
        }
        return new Hand(
                calculateMirroredTransform(parent),
                this.getBoundingBox().cloneBox(), // coordinate system is reflected so box must not be reflected!
                mirroredParent.orElse(parent), this.getAncestor(), true);
    }

    @Override
    public LowerArm getParent() {
        if (!(super.getParent() instanceof  LowerArm)) {
            System.err.println("Parent of hand is not lower arm?!");
            return null;
        } else {
            return (LowerArm) super.getParent();
        }
    }

    /**
     * @return the translation to move the joint between this element and its parent from this origin somewhere else.
     */
    public static Vector3f getLocalTranslationFromJoint(BoundingBox boundingBox) {
        return new Vector3f(-boundingBox.getXLength()/2f, -boundingBox.getYLength(), -boundingBox.getZLength()/2f);
    }
}
