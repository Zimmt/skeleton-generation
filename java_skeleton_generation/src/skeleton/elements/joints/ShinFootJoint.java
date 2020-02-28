package skeleton.elements.joints;

import skeleton.elements.terminal.Foot;
import skeleton.elements.terminal.TerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;

public class ShinFootJoint extends TwoAngleBasedJoint {

    private static float minFrontAngleShin = (float) -Math.toRadians(45);
    private static float maxFrontAngleShin = (float) Math.toRadians(45);
    private static float minSideAngleShin = (float) -Math.toRadians(170);

    public ShinFootJoint(TerminalElement parent, Point3f position) {
        super(parent, position, minFrontAngleShin, maxFrontAngleShin, minSideAngleShin, 0f);
    }

    public TransformationMatrix calculateChildTransform(BoundingBox childBoundingBox) {
        TransformationMatrix transform = super.calculateChildTransform(childBoundingBox);
        transform.translate(Foot.getLocalTranslationFromJoint(childBoundingBox));
        return transform;
    }

    public ShinFootJoint calculateMirroredJoint(TerminalElement mirroredParent) {
        return new ShinFootJoint(mirroredParent, calculateMirroredJointPosition(mirroredParent));
    }
}
