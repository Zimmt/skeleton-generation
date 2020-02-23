package skeleton.elements.joints;

import skeleton.elements.terminal.TerminalElement;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

/**
 * The joints are saved in the parent element and can be used to calculate the transform for the connected child
 */
public abstract class Joint {

    Point3f position; // position in the coordinate system of the parent element

    public Joint(Point3f position) {
        this.position = position;
    }

    public abstract TransformationMatrix calculateChildTransform(TerminalElement parent);
    public abstract Joint calculateMirroredJoint(TerminalElement parent, TerminalElement mirroredParent);

    /**
     * Mirrors the joint by transforming the position to world coordinates,
     * reflect it and then transforming it back.
     */
    protected Point3f calculateMirroredJointPosition(TerminalElement parent, TerminalElement mirroredParent) {
        Point3f mirroredPosition = new Point3f(position); // local coordinates
        parent.calculateWorldTransform().applyOnPoint(mirroredPosition); // global coordinates
        mirroredPosition.z = -mirroredPosition.z; // global coordinates mirrored

        TransformationMatrix.getInverse(mirroredParent.calculateWorldTransform()).applyOnPoint(mirroredPosition);

        return mirroredPosition;
    }
}
