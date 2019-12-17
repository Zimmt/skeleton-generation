package skeleton.elements.terminal;

import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

/**
 * Wirbel
 */
public class Vertebra extends TerminalElement {

    private final String kind = "vertebra";

    public Vertebra(TransformationMatrix transform, Point3f jointRotationPoint, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor) {
        super(transform, jointRotationPoint, boundingBox, parent, ancestor);
    }

    public String getKind() {
        return kind;
    }

    public boolean isMirrored() { return false; }

    public Vertebra calculateMirroredElement(TerminalElement parent) {
        System.out.println("Tried to mirror an element that should not be mirrored!");
        return null;
    }
}
