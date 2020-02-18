package skeleton.elements.nonterminal;

import skeleton.elements.terminal.TerminalElement;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public class BackPart extends NonTerminalElement {

    private final String kind = "back part";
    private float startPosition;

    public BackPart(TransformationMatrix transform, Point3f jointRotationPoint, TerminalElement parent, NonTerminalElement ancestor, float backPartStartPosition) {
        super(transform, jointRotationPoint, parent, ancestor);
        this.startPosition = backPartStartPosition;
    }

    public String getKind() {
        return kind;
    }

    public float getStartPosition() {
        return startPosition;
    }

    public boolean isMirrored() { return false; }
}
