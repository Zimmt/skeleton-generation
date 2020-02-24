package skeleton.elements.terminal;

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

    public String getKind() {
        return kind;
    }

    public boolean isMirrored() { return true; }

    public Hand calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        if (parent.isMirrored() && mirroredParent.isEmpty()) {
            System.err.println("Cannot mirror child when mirrored parent is not given!");
        }
        return new Hand(
                calculateMirroredTransform(parent),
                this.getBoundingBox().cloneBox(), // coordinate system is reflected so box must not be reflected!
                mirroredParent.orElse(parent), this.getAncestor());
    }

    /**
     * @return the translation to move the joint between this element and its parent from this origin somewhere else.
     */
    public static Vector3f getLocalTranslationFromJoint(BoundingBox boundingBox) {
        return new Vector3f(-boundingBox.getXLength(), -boundingBox.getYLength(), -boundingBox.getZLength()/2f);
    }
}
