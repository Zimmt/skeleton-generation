package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import java.util.Optional;

public abstract class TerminalElement extends SkeletonPart {

    private BoundingBox boundingBox;

    public TerminalElement(TransformationMatrix transform, Point3f jointRotationPoint, BoundingBox boundingBox,
                           TerminalElement parent, NonTerminalElement ancestor) {
        super(transform, jointRotationPoint, parent, ancestor);

        this.boundingBox = boundingBox;
    }

    public abstract TerminalElement calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent);

    public BoundingBox getBoundingBox() { return boundingBox; }

    public boolean isTerminal() {
        return true;
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
     * Mirrors the joint rotation point by transforming it to world coordinates,
     * reflect it and then transforming it back.
     * The parent elements are needed, as the joint rotation point is presented in the coordinate system of the parent.
     * @param mirroredParent if mirrored parent is given the element is assumed to have a mirrored parent and
     *                      the inverse world transform of it is used to transform the joint rotation point back
     *                      otherwise the parent's transform is used
     * @return new joint rotation point
     */
    protected Point3f calculateMirroredJointRotationPoint(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        TransformationMatrix worldTransform = parent.calculateWorldTransform();

        Point3f mirroredJointRotationPoint = new Point3f(this.getJointRotationPoint()); // local coordinates
        worldTransform.applyOnPoint(mirroredJointRotationPoint); // global coordinates
        mirroredJointRotationPoint.z = -mirroredJointRotationPoint.z; // global coordinates mirrored
        if (mirroredParent.isEmpty()) {
            TransformationMatrix.getInverse(worldTransform).applyOnPoint(mirroredJointRotationPoint);
        } else {
            TransformationMatrix.getInverse(mirroredParent.get().calculateWorldTransform()).applyOnPoint(mirroredJointRotationPoint);
        }
        return mirroredJointRotationPoint; // local coordinates mirrored
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
}
