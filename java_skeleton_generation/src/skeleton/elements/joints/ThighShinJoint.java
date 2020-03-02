package skeleton.elements.joints;

import skeleton.elements.terminal.Shin;
import skeleton.elements.terminal.TerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

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

    Boolean getTurnDirectionNearerToFloor() {
        Vector3f testVectorParent = new Vector3f(0f, -1f, 0f);
        parent.calculateWorldTransform().applyOnVector(testVectorParent);
        Vector3f testVectorWorld = new Vector3f(0f, -1f, 0f);

        float eps = 0.01f;
        float wantedAngle = testVectorWorld.angle(testVectorParent);
        if (Math.abs(wantedAngle - currentAngle) < eps) {
            return null;
        } else {
            return currentAngle < wantedAngle;
        }
    }

    public ThighShinJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new ThighShinJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
