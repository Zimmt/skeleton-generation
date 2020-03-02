package skeleton.elements.joints;

import skeleton.elements.terminal.TerminalElement;
import skeleton.elements.terminal.Thigh;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public class PelvicThighJoint extends XZAngleBasedJoint {

    private static float maxFrontAnglePelvic = (float) Math.toRadians(170);
    private static float minSideAnglePelvic = (float) -Math.toRadians(170);
    private static float maxSideAnglePelvic = (float) Math.toRadians(90);

    public PelvicThighJoint(TerminalElement parent, Point3f position) {
        super(parent, position, 0f, maxFrontAnglePelvic, minSideAnglePelvic, maxSideAnglePelvic);
        currentSecondAngle = (float) - Math.toRadians(45);
    }

    /**
     * child transform
     */
    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = super.calculateChildTransform(childBoundingBox);
        transform.translate(Thigh.getLocalTranslationFromJoint(childBoundingBox));
        return transform;
    }

    public PelvicThighJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new PelvicThighJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
