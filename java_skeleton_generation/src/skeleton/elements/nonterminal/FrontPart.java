package skeleton.elements.nonterminal;

import skeleton.elements.terminal.TerminalElement;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public class FrontPart extends NonTerminalElement {

    private final String kind = "front part";
    private float frontPartStartPosition;

    public FrontPart(TransformationMatrix transform, Point3f jointRotationPoint, TerminalElement parent, NonTerminalElement ancestor, float frontPartStartPosition) {
        super(transform, jointRotationPoint, parent, ancestor);
        this.frontPartStartPosition = frontPartStartPosition;
    }

    public String getKind() {
        return kind;
    }

    public float getFrontPartStartPosition() {
        return frontPartStartPosition;
    }

    public boolean isMirrored() { return false; }
}
