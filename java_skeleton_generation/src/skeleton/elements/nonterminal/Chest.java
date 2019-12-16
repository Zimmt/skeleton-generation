package skeleton.elements.nonterminal;

import skeleton.elements.terminal.TerminalElement;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public class Chest extends NonTerminalElement {

    private final String kind = "chest";

    public Chest(TransformationMatrix transform, Point3f jointRotationPoint, TerminalElement parent, NonTerminalElement ancestor) {
        super(transform, jointRotationPoint, parent, ancestor);
    }

    public String getKind() {
        return kind;
    }

    public boolean isMirrored() { return false; }
}
