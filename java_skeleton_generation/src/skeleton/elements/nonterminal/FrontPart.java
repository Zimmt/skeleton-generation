package skeleton.elements.nonterminal;

import skeleton.elements.terminal.TerminalElement;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public class FrontPart extends NonTerminalElement {

    private final String kind = "front part";
    private float endPosition;

    public FrontPart(TransformationMatrix transform, Point3f jointRotationPoint, TerminalElement parent, NonTerminalElement ancestor, float endPosition) {
        super(transform, jointRotationPoint, parent, ancestor);
        this.endPosition = endPosition;
    }

    public String getKind() {
        return kind;
    }

    public float getEndPosition() {
        return endPosition;
    }

    public boolean isMirrored() { return false; }
}
