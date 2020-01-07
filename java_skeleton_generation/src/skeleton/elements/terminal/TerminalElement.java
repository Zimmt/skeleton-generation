package skeleton.elements.terminal;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public abstract class TerminalElement extends SkeletonPart {

    private BoundingBox boundingBox;

    public TerminalElement(TransformationMatrix transform, Point3f jointRotationPoint, BoundingBox boundingBox,
                           TerminalElement parent, NonTerminalElement ancestor) {
        super(transform, jointRotationPoint, parent, ancestor);

        this.boundingBox = boundingBox;
    }

    public abstract TerminalElement calculateMirroredElement(TerminalElement parent);

    public BoundingBox getBoundingBox() { return boundingBox; }

    public boolean isTerminal() {
        return true;
    }

    protected TransformationMatrix calculateMirroredTransform() {
        return TransformationMatrix.reflectTransformAtWorldXYPlane(this);
    }

    protected Point3f calculateMirroredJointRotationPoint() {
        TransformationMatrix worldTransform = this.calculateWorldTransform();

        Point3f mirroredJointRotationPoint = new Point3f(this.getJointRotationPoint()); // local coordinates
        worldTransform.applyOnPoint(mirroredJointRotationPoint); // global coordinates
        mirroredJointRotationPoint.z = -mirroredJointRotationPoint.z; // global coordinates mirrored
        TransformationMatrix.getInverse(worldTransform).applyOnPoint(mirroredJointRotationPoint); // local coordinates mirrored

        return mirroredJointRotationPoint;
    }
}
