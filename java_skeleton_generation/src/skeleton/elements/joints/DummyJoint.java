package skeleton.elements.joints;

import skeleton.elements.terminal.TerminalElement;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class DummyJoint extends Joint {

    public DummyJoint(Point3f position) {
        super(position);
    }

    public TransformationMatrix calculateChildTransform(TerminalElement parent) {

        return new TransformationMatrix(new Vector3f(position));
    }

    public DummyJoint calculateMirroredJoint(TerminalElement parent, TerminalElement mirroredParent) {

        return new DummyJoint(new Point3f(position.x, position.y, -position.z));
    }
}
