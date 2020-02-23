package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import java.util.Optional;

public abstract class TerminalElement extends SkeletonPart {

    private TransformationMatrix transform; // position and rotation in relation to the coordinate system of parent (parent origin is origin for this transform)
    private BoundingBox boundingBox;

    public TerminalElement(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor) {
        super(parent, ancestor);
        this.transform = transform;
        this.boundingBox = boundingBox;
    }

    public abstract TerminalElement calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent);

    public BoundingBox getBoundingBox() { return boundingBox; }

    public boolean isTerminal() {
        return true;
    }

    /**
     * @return the transformation matrix that transforms from the local coordinate system of this skeleton part
     * to the world space
     */
    public TransformationMatrix calculateWorldTransform() {
        TransformationMatrix worldTransform = new TransformationMatrix(transform);
        TerminalElement parent = this;
        while (parent.hasParent()) {
            parent = parent.getParent();
            worldTransform = TransformationMatrix.multiply(parent.getTransform(), worldTransform);
        }
        return worldTransform;
    }

    public Point3f getWorldPosition() {
        TransformationMatrix t = calculateWorldTransform();
        Point3f position = new Point3f(); // origin
        t.applyOnPoint(position);

        return position;
    }

    protected TransformationMatrix calculateMirroredTransform(TerminalElement parent) {
        TransformationMatrix result;

        if (parent.isMirrored()) {
            TransformationMatrix xp = parent.calculateLeftToRightTransform();
            TransformationMatrix xc = calculateLeftToRightTransform();

            // change to left handed coordinate system, transform to parent coordinates, change to right handed coordinate system
            result = TransformationMatrix.multiply(TransformationMatrix.multiply(xp, getTransform()), xc);
        } else {
            TransformationMatrix inverseParentWorldTransform = TransformationMatrix.getInverse(parent.calculateWorldTransform());
            TransformationMatrix reflection = TransformationMatrix.getReflectionTransformZ();
            TransformationMatrix childWorldTransform = calculateWorldTransform();
            TransformationMatrix xc = calculateLeftToRightTransform();

            // change to left handed coordinate system, transform to world coordinates,
            // reflect (get right handed system again), then transform to parent coordinates
            result = TransformationMatrix.multiply(TransformationMatrix.multiply(TransformationMatrix.multiply(
                    inverseParentWorldTransform, reflection), childWorldTransform), xc);
        }
        return result;
    }

    /**
     * @return a transform that transforms the left/right handed coordinate system of this element into a right/left handed one
     * by moving the origin along the z-axis and then flipping the z-axis
     */
    public TransformationMatrix calculateLeftToRightTransform() {
        TransformationMatrix reflection = TransformationMatrix.getReflectionTransformZ();
        TransformationMatrix translation = new TransformationMatrix(boundingBox.getZVector());

        return TransformationMatrix.multiply(translation, reflection);
    }

    public TransformationMatrix getTransform() {
        return transform;
    }
}
