package skeleton.elements.terminal;

import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Vector3f;
import java.util.Optional;

public class Foot extends TerminalElement {

    private final String kind = "foot";

    public Foot(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor) {
        super(transform, boundingBox, parent, ancestor);
    }

    private Foot(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor, boolean isMirroredVersion) {
        super(transform, boundingBox, parent, ancestor);
        super.isMirroredVersion = isMirroredVersion;
    }

    public String getKind() {
        Vector3f localY = new Vector3f(0f, 1f, 0f);
        calculateWorldTransform().applyOnVector(localY);
        localY.z = 0f;
        float angle1 = localY.angle(new Vector3f(1f, 0f, 0f));
        float angle2 = localY.angle(new Vector3f(-1f, 0f, 0f));
        if (angle1 < Math.toRadians(45) || angle2 < Math.toRadians(45)) {
            return "hand";
        } else {
            return "hoof";
        }
    }

    public boolean canBeMirrored() { return true; }

    public Foot calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        if (parent.canBeMirrored() && mirroredParent.isEmpty()) {
            System.err.println("Cannot mirror child when mirrored parent is not given!");
        }
        return new Foot(
                calculateMirroredTransform(parent),
                this.getBoundingBox().cloneBox(), // coordinate system is reflected so box must not be reflected!
                mirroredParent.orElse(parent), this.getAncestor(), true);
    }

    /**
     * @return the translation to move the joint between this element and its parent from this origin somewhere else.
     */
    public static Vector3f getLocalTranslationFromJoint(BoundingBox boundingBox) {
        return new Vector3f(-boundingBox.getXLength()/2f, -boundingBox.getYLength(), -boundingBox.getZLength()/2f);
    }
}
