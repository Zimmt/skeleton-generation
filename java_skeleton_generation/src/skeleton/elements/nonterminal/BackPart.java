package skeleton.elements.nonterminal;

import skeleton.elements.terminal.TerminalElement;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Tuple2f;

public class BackPart extends NonTerminalElement {

    private final String kind = "back part";
    private Tuple2f pelvicSpineInterval;

    public BackPart(TransformationMatrix transform, Point3f jointRotationPoint, TerminalElement parent, NonTerminalElement ancestor, Tuple2f pelvicSpineInterval) {
        super(transform, jointRotationPoint, parent, ancestor);

        this.pelvicSpineInterval = pelvicSpineInterval;
    }

    public String getKind() {
        return kind;
    }

    public Tuple2f getPelvicSpineInterval() {
        return pelvicSpineInterval;
    }

    public boolean isMirrored() { return false; }
}
