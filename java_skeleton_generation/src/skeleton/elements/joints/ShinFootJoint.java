package skeleton.elements.joints;

import javax.vecmath.Point3f;

public class ShinFootJoint extends TwoAngleBasedJoint {

    private static float maxTheta = (float) Math.toRadians(45); // todo 90?
    private static float minPhi = 0f; // todo (float) -Math.toRadians(45);
    private static float maxPhi = (float) Math.toRadians(45);

    public ShinFootJoint(Point3f position, boolean floorRestricted) {
        super(position, maxTheta, minPhi, maxPhi, floorRestricted);
    }
}
