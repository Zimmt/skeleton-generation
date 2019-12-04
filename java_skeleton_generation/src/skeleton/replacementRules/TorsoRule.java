package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.Torso;
import skeleton.elements.terminal.Vertebra;
import util.BoundingBox;
import util.CubicBezierCurve;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.*;

public class TorsoRule extends ReplacementRule {

    private final String inputID = "torso";
    private final int minVertebraCount = 4;
    private final int maxVertebraCount = 4;
    private Random random = new Random();

    public String getInputID() {
        return inputID;
    }

    /**
     * Fits vertebrae on spine so that the spine pierces the bounding boxes in the middle of the left and the right
     * side. (They are only rotated around the z-axis.)
     */
    public List<SkeletonPart> apply(SkeletonPart skeletonPart) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(skeletonPart)) {
            return Arrays.asList(skeletonPart);
        }
        Torso torso = (Torso) skeletonPart;

        //System.out.println("Apply " + inputID + " rule");

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.setXLength(1f);
        Vector3f negativeHalfBoxHeight = new Vector3f(0f, -boundingBox.getYLength() / 2f, 0f);

        CubicBezierCurve spine = torso.getGenerator().getSpineLocation();
        int vertebraCount = random.nextInt(maxVertebraCount + 1 - minVertebraCount) + minVertebraCount;

        ArrayList<SkeletonPart> generatedParts = new ArrayList<>();
        Vertebra parent = new Vertebra(new TransformationMatrix(), new Point3f(), boundingBox, null, torso); // dummy parent

        for (int i = 0; i < vertebraCount; i++) {
            float t = torso.getSpineInterval().x + (float) i / (float) vertebraCount * torso.getSpineIntervalLength();
            float tPlus1 = t + 1f / (float) vertebraCount * torso.getSpineIntervalLength();

            // we have the world position of the spine and we have to get something that is relative to parent
            TransformationMatrix transform = TransformationMatrix.getInverse(parent.getWorldTransform());

            float angle = getSpineAngle(spine, t, tPlus1);
            transform.rotateAroundZ(angle);
            Vector3f position = new Vector3f(spine.apply3d(t)); // world position
            transform.translate(position);

            BoundingBox childBox = boundingBox.cloneBox();

            Point3f jointRotationPoint = new Point3f(position);
            Vector3f offset = new Vector3f(childBox.getYVector());
            offset.add(childBox.getZVector());
            offset.scale(0.5f);
            offset.add(childBox.getXVector());
            jointRotationPoint.add(offset);


            Vertebra child;
            if (i == 0) { // this is the real parent (dummy parent was used to calculate it)
                child = new Vertebra(transform, jointRotationPoint, childBox, null, torso); // root
            } else {
                child = new Vertebra(transform, jointRotationPoint, childBox, parent, torso);
                parent.addChild(child);
            }

            generatedParts.add(child);

            // Move child down a negative half bounding box height
            // so that spine is not at the bottom of the vertebra but pierces
            // the bounding box in the center of the left and right side.
            // Do this after the generation of the child to be able to use
            // the world transform method of the child.
            Vector3f transformedNegativeBoxHeight = new Vector3f(negativeHalfBoxHeight);
            child.getWorldTransform().applyOnVector(transformedNegativeBoxHeight);
            child.getTransform().translate(transformedNegativeBoxHeight);

            parent = child;
        }

        return generatedParts;
    }

    /**
     * @param t1 first point on spine
     * @param t2 second point on spine
     * @return angle between spine vector (from first to second point on spine) and the vector (1,0,0)
     */
    private float getSpineAngle(CubicBezierCurve spine, float t1, float t2) {
        Point3f position1 = spine.apply3d(t1);
        Point3f position2 = spine.apply3d(t2);
        Point3f diff = position2;
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
