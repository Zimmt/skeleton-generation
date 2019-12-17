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

    public BoundingBox calculateWorldBoundingBox() {
        TransformationMatrix worldTransform = this.getWorldTransform();
        BoundingBox transformedBox = this.getBoundingBox().cloneBox();
        transformedBox.transform(worldTransform);
        return transformedBox;
    }

    public boolean isTerminal() {
        return true;
    }

    protected TransformationMatrix calculateMirroredTransform() {
        TransformationMatrix mirroredTransform = new TransformationMatrix(this.getTransform());
        mirroredTransform.reflectZ();

        return mirroredTransform;
    }

    protected Point3f calculateMirroredJointRotationPoint() {
        Point3f mirroredJointRotationPoint = new Point3f(this.getJointRotationPoint());
        TransformationMatrix.reflectZTransform().applyOnPoint(mirroredJointRotationPoint);

        return mirroredJointRotationPoint;
    }
}
