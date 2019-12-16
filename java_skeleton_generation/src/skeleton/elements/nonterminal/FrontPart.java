package skeleton.elements.nonterminal;

import skeleton.elements.terminal.TerminalElement;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Tuple2f;

public class FrontPart extends NonTerminalElement {

    private final String kind = "front part";
    private Tuple2f shoulderSpineInterval;

    public FrontPart(TransformationMatrix transform, Point3f jointRotationPoint, TerminalElement parent, NonTerminalElement ancestor, Tuple2f shoulderSpineInterval) {
        super(transform, jointRotationPoint, parent, ancestor);
        this.shoulderSpineInterval = shoulderSpineInterval;
    }

    public String getKind() {
        return kind;
    }

    public Tuple2f getShoulderSpineInterval() {
        return shoulderSpineInterval;
    }

    public boolean isMirrored() { return false; }
}
