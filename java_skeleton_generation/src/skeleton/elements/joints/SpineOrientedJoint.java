package skeleton.elements.joints;

import skeleton.SkeletonGenerator;
import skeleton.SpinePart;
import skeleton.elements.terminal.TerminalElement;
import util.TransformationMatrix;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector3f;

public class SpineOrientedJoint extends Joint {

    private SpinePart spinePart;
    private float spinePosition; // where the joint rotation point lies on spine, as bezier curve parameter
    private boolean spineDirection; // if child will be spawned in positive (true) or negative (false) direction of bezier curve
    private SkeletonGenerator skeletonGenerator; // skeleton generator is only needed because root vertebra has no parent where generator can be accessed

    float spineWidth;

    /**
     * @param spineDirection true: positive direction, false: negative direction
     */
    public SpineOrientedJoint(Point3f position, SpinePart spinePart, float spinePosition, boolean spineDirection, SkeletonGenerator skeletonGenerator) {
        super(position);
        this.spinePart = spinePart;
        this.spinePosition = spinePosition;
        this.spineDirection = spineDirection;
        this.skeletonGenerator = skeletonGenerator;
    }

    public void setSpineWidth(float spineWidth) {
        if (spineWidth <= 0) {
            System.err.println("Invalid spine width");
        }
        this.spineWidth = spineWidth;
    }

    /**
     * child width: spinePosition to spinePosition +- spineWidth
     */
    public TransformationMatrix calculateChildTransform(TerminalElement parent) {
        if (spineWidth <= 0) {
            System.err.println("Set child spine width first before generating child transform is possible");
            return null;
        }

        Tuple2f spineInterval;
        if (spineDirection) {
            spineInterval = new Point2f(spinePosition, spinePosition + spineWidth);
        } else {
            spineInterval = new Point2f(spinePosition - spineWidth, spinePosition);
        }

        SpinePart spinePart1 = spinePart;
        SpinePart spinePart2 = spinePart;
        if (spineInterval.x < 0) {
            spineInterval.x += 1f;
            spinePart1 = SpinePart.getSmallerSpinePart(spinePart1);
            if (spinePart1 == null) {
                return null;
            }
        }
        if (spineInterval.y > 1) {
            spineInterval.y -= 1f;
            spinePart2 = SpinePart.getBiggerSpinePart(spinePart2);
            if (spinePart2 == null) {
                return null;
            }
        }
        float angle = getSpineAngle(spinePart1, spinePart2, spineInterval.x, spineInterval.y);

        Vector3f worldPosition = new Vector3f(skeletonGenerator.getSkeletonMetaData().getSpine().getPart(spinePart).apply3d(spinePosition));
        TransformationMatrix childWorldTransform = new TransformationMatrix(worldPosition);
        childWorldTransform.rotateAroundZ(angle);

        TransformationMatrix inverseParentWorldTransform ;
        if (parent == null) {
            inverseParentWorldTransform = new TransformationMatrix();
        } else {
            inverseParentWorldTransform = TransformationMatrix.getInverse(parent.calculateWorldTransform());
        }

        return TransformationMatrix.multiply(inverseParentWorldTransform, childWorldTransform);
    }

    public Joint calculateMirroredJoint(TerminalElement parent, TerminalElement mirroredParent) {
        return new SpineOrientedJoint(calculateMirroredJointPosition(parent, mirroredParent),
                spinePart, spinePosition, spineDirection, skeletonGenerator);
    }

    /**
     * Interval borders have to be ascending.
     * @param spinePart1 the spine part for the first point
     * @param spinePart2 the spine part for the second point
     * @param t1 first point on spine
     * @param t2 second point on spine
     * @return angle between spine vector (from first to second point on spine) and the vector (1,0,0)
     */
    private float getSpineAngle(SpinePart spinePart1, SpinePart spinePart2, float t1, float t2) {
        Point3f position1 = skeletonGenerator.getSkeletonMetaData().getSpine().getPart(spinePart1).apply3d(t1);
        Point3f position2 = skeletonGenerator.getSkeletonMetaData().getSpine().getPart(spinePart2).apply3d(t2);
        Point3f diff = new Point3f(position2);
        diff.sub(position1);
        Vector3f spineVector = new Vector3f(diff);

        float angle = spineVector.angle(new Vector3f(1f, 0f, 0f));

        // determine in which direction we should turn
        if (position1.y > position2.y) {
            angle = -angle;
        }
        return angle;
    }
}
