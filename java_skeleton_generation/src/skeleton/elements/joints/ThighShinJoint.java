package skeleton.elements.joints;

import javax.vecmath.Point3f;

public class ThighShinJoint extends OneAngleBasedJoint {

    private static float minAngle = 0f;
    private static float maxAngle = (float) Math.toRadians(90); // todo 180?

    public ThighShinJoint(Point3f position) {
        super(position, minAngle, maxAngle);
    }
}
