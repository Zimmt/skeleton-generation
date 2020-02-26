package skeleton.elements.joints;

import skeleton.elements.terminal.TerminalElement;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Random;

public class TwoAngleBasedJoint extends Joint {

    // max angles in radians
    private float maxTheta; // angle from polar axis (this determines a circle)
    // angle from reference direction (this determines a point on the circle)
    private float minPhi;
    private float maxPhi;

    private boolean floorRestricted;
    private Random random = new Random(); // todo use some kind of normal distribution?

    // needed for floor calculations
    private float childLengthToNextJoint;
    private float restExtremityLength; // length of extremity from joint of child to floor

    public TwoAngleBasedJoint(Point3f position, float maxTheta, float minPhi, float maxPhi, boolean floorRestricted) {
        super(position);
        float eps = 0.01f;
        if (Math.abs(maxTheta) < -eps || maxTheta > Math.toRadians(180)+eps) {
            System.err.println("Invalid theta angle");
        }
        if (minPhi > maxPhi || Math.abs(minPhi) > Math.toRadians(360)+eps || Math.abs(maxPhi) > Math.toRadians(360)+eps) {
            System.err.println("Invalid phi angle");
        }

        this.maxTheta = maxTheta;
        this.minPhi = minPhi;
        this.maxPhi = maxPhi;
        this.floorRestricted = floorRestricted;
    }

    /**
     * Values needed for floor restricted calculations of child transform
     * @param childLengthToNextJoint length from this joint to child joint
     * @param restExtremityLength max length from child joint to floor
     */
    public void setLengthsToFloor(float childLengthToNextJoint, float restExtremityLength) {
        this.childLengthToNextJoint = childLengthToNextJoint;
        this.restExtremityLength = restExtremityLength;
    }

    public TransformationMatrix calculateChildTransform(TerminalElement parent) {
        /*if (floorRestricted && (childLengthToNextJoint <= 0 || restExtremityLength <= 0)) {
            System.err.println("Cannot calculate child transform, set lengths first!");
            return null;
        }*/

        // todo floor restriction

        float theta = random.nextFloat() * maxTheta;
        float phi = (random.nextFloat() * (maxPhi - minPhi)) + minPhi;

        System.out.println("Two angle rotation " + Math.toDegrees(theta) + ", " + Math.toDegrees(phi));

        TransformationMatrix transform = new TransformationMatrix(new Vector3f(position));
        transform.rotateAroundY(-phi);
        transform.rotateAroundZ(-theta);

        return transform;
    }

    public TwoAngleBasedJoint calculateMirroredJoint(TerminalElement parent, TerminalElement mirroredParent) {
        return new TwoAngleBasedJoint(calculateMirroredJointPosition(parent, mirroredParent), maxTheta, -minPhi, -maxPhi, floorRestricted);
    }
}
