package skeleton.elements.joints;

import skeleton.elements.terminal.Shin;
import skeleton.elements.terminal.TerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public class ThighShinJoint extends OneAngleBasedJoint {

    private static float maxAngleThigh = (float) Math.toRadians(170);

    public ThighShinJoint(TerminalElement parent, Point3f position) {
        super(parent, position, 0f, maxAngleThigh);
        currentAngle = (float) Math.toRadians(90);
    }

    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = super.calculateChildTransform(childBoundingBox);
        transform.translate(Shin.getLocalTranslationFromJoint(childBoundingBox));
        return transform;
    }

    public ThighShinJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new ThighShinJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
