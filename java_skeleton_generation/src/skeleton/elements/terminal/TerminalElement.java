package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public abstract class TerminalElement extends SkeletonPart {

    private BoundingBox boundingBox;

    public TerminalElement(TransformationMatrix transform, Point3f jointRotationPoint, BoundingBox boundingBox,
                           SkeletonPart parent, SkeletonPart ancestor) {
        super(transform, jointRotationPoint, parent, ancestor);

        this.boundingBox = boundingBox;
    }

    public BoundingBox getBoundingBox() { return boundingBox; }

    public boolean isTerminal() {
        return true;
    }
}
