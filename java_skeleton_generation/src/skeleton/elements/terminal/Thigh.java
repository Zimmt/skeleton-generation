package skeleton.elements.terminal;

import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

/**
 * Oberschenkel
 */
public class Thigh extends TerminalElement {

    private final String kind = "thigh";

    public Thigh(TransformationMatrix transform, Point3f jointRotationPoint, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor) {
        super(transform, jointRotationPoint, boundingBox, parent, ancestor);
    }

    public String getKind() {
        return kind;
    }

    public boolean isMirrored() { return true; }
}
