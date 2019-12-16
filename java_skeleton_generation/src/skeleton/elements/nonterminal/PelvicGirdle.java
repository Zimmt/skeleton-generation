package skeleton.elements.nonterminal;

import skeleton.elements.SkeletonPart;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

/**
 * Hüftgürtel
 */
public class PelvicGirdle extends NonTerminalElement {

    private final String kind = "pelvic girdle";

    public PelvicGirdle(TransformationMatrix transform, Point3f jointRotationPoint, SkeletonPart parent, SkeletonPart ancestor) {
        super(transform, jointRotationPoint, parent, ancestor);
    }

    public String getKind() {
        return kind;
    }

    public boolean isMirrored() { return false; }
}
