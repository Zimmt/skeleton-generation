package skeleton.elements.joints;

import skeleton.elements.terminal.TerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class DummyJoint extends Joint {

    public DummyJoint(TerminalElement parent, Point3f position) {
        super(parent, position);
    }

    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        return new TransformationMatrix(new Vector3f(position));
    }

    public DummyJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new DummyJoint(mirroredParent, new Point3f(position.x, position.y, -position.z));
    }
}
