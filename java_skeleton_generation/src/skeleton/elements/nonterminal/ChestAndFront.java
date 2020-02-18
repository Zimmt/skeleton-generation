package skeleton.elements.nonterminal;

import skeleton.elements.terminal.TerminalElement;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public class ChestAndFront extends NonTerminalElement {

    private final String kind = "chest and front";

    public ChestAndFront(TransformationMatrix transform, Point3f jointRotationPoint, TerminalElement parent, NonTerminalElement ancestor) {
        super(transform, jointRotationPoint, parent, ancestor);
    }

    public String getKind() {
        return kind;
    }

    public boolean isMirrored() { return false; }
}
