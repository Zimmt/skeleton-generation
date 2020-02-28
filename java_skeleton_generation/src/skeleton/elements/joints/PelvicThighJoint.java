package skeleton.elements.joints;

import skeleton.elements.terminal.TerminalElement;
import skeleton.elements.terminal.Thigh;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class PelvicThighJoint extends TwoAngleBasedJoint {

    private static float maxFrontAnglePelvic = (float) Math.toRadians(170);
    private static float minSideAnglePelvic = (float) -Math.toRadians(170);

    private float sideAngleOffset; // the angle between local x axis and global x axis

    public PelvicThighJoint(TerminalElement parent, Point3f position) {
        super(parent, position, 0f, maxFrontAnglePelvic, minSideAnglePelvic, 0f);
        initializeSideAngle();
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

    /**
     * Initializes side angle so that the coordinate system of the child is oriented like the world coordinate system
     */
    private void initializeSideAngle() {
        Vector3f horizontal = new Vector3f(1f, 0f, 0f);
        Vector3f localXDir = new Vector3f(1f, 0f, 0f);
        parent.calculateWorldTransform().applyOnVector(localXDir);
        this.sideAngleOffset = localXDir.angle(horizontal);
        if (localXDir.y > 0) {
            this.sideAngleOffset = -sideAngleOffset;
        }

        setMaxSideAngle(sideAngleOffset);
        setCurrentSideAngle(sideAngleOffset);
    }
}
