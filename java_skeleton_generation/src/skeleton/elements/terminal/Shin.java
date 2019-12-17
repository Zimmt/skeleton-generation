package skeleton.elements.terminal;

import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

/**
 * Schienbein
 */
public class Shin extends TerminalElement {

    private final String kind = "shin";

    public Shin(TransformationMatrix transform, Point3f jointRotationPoint, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor) {
        super(transform, jointRotationPoint, boundingBox, parent, ancestor);
    }

    public String getKind() {
        return kind;
    }

    public boolean isMirrored() { return true; }

    public Shin calculateMirroredElement(TerminalElement parent) {
        return new Shin(
                calculateMirroredTransform(),
                calculateMirroredJointRotationPoint(),
                this.getBoundingBox().cloneBox(), // coordinate system is reflected so box must not be reflected!
                parent, this.getAncestor());
    }
}
