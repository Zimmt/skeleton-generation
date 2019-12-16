package skeleton.elements.nonterminal;

import skeleton.elements.terminal.TerminalElement;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Tuple2f;

public class ShoulderGirdle extends NonTerminalElement {

    private final String kind = "shoulder girdle";
    private Tuple2f spineInterval;

    public ShoulderGirdle(TransformationMatrix transform, Point3f jointRotationPoint, TerminalElement parent, NonTerminalElement ancestor, Tuple2f spineInterval) {
        super(transform, jointRotationPoint, parent, ancestor);

        this.spineInterval = spineInterval;
    }

    public String getKind() {
        return kind;
    }

    public Tuple2f getSpineInterval() {
        return spineInterval;
    }

    public boolean isMirrored() { return false; }
}
