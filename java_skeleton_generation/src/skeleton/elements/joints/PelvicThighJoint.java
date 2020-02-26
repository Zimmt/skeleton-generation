package skeleton.elements.joints;

import javax.vecmath.Point3f;

public class PelvicThighJoint extends TwoAngleBasedJoint {

    private static float maxTheta = (float) Math.toRadians(90); // todo 180?
    private static float minPhi = 0f;
    private static float maxPhi = (float) Math.toRadians(90);

    public PelvicThighJoint(Point3f position, boolean floorRestricted) {
        super(position, maxTheta, minPhi, maxPhi, floorRestricted);
    }
}
