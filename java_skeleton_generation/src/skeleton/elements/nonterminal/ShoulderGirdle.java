package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Tuple2f;

public class ShoulderGirdle extends NonTerminalElement {

    private final String kind = "shoulder girdle";
    private Tuple2f spineInterval;

    public ShoulderGirdle(Tuple2f spineInterval, TransformationMatrix transform, Point3f jointRotationPoint, SkeletonPart parent, SkeletonPart ancestor) {
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
