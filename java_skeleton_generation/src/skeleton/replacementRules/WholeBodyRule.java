package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.WholeBody;
import skeleton.elements.terminal.Head;
import skeleton.elements.terminal.Vertebra;
import util.BoundingBox;
import util.CubicBezierCurve;
import util.TransformationMatrix;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class WholeBodyRule extends ReplacementRule {

    private final String inputID = "whole body";

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart skeletonPart) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(skeletonPart)) {
            return Arrays.asList(skeletonPart);
        }
        WholeBody wholeBody = (WholeBody) skeletonPart;

        //System.out.println("Apply " + inputID + " rule");

        if (wholeBody.hasChildren()) {
            System.err.println("Whole body should not have children before this rule is applied.");
        }

        List<Float> intervals = wholeBody.getGenerator().getSpineLocation().getIntervalsByGradientEpsilon(0.5f);
        System.out.println("spine intervals: " + intervals);

        /*Tuple2f spineInterval = null;
        if (intervals.isEmpty()) {
            spineInterval = new Point2f(1f/3f, 2f/3f); // this should not happen
            System.out.println("no appropriate spine interval found");

        } else if (intervals.size() == 2) {
            spineInterval = new Point2f(intervals.get(0), intervals.get(1));

        } else { // find interval that contains 0.5 or is first after 0.5
            for (int i = 0; i < intervals.size(); i += 2) {
                if (intervals.get(i) >= 0.5f) {
                    spineInterval = new Point2f(intervals.get(i), intervals.get(i+1));
                    break;
                }
            }
            if (spineInterval == null) {
                int i = intervals.size() - 2;
                spineInterval = new Point2f(intervals.get(i), intervals.get(i+1));
            }
        }*/

        List<SkeletonPart> generatedParts = new ArrayList<>();

        // spine
        Tuple2f spineInterval = new Point2f(0f, 1f);
        Vertebra dummyParent = new Vertebra(new TransformationMatrix(), new Point3f(), BoundingBox.defaultBox(), null, wholeBody); // dummy parent
        List<SkeletonPart> vertebrae = generateVertebraInInterval(wholeBody, spineInterval, 10,
                dummyParent, true, Optional.empty());

        generatedParts.addAll(vertebrae);

        // head
        Head head = generateHead(wholeBody, new Vector3f(2f, 1f, 1.5f), vertebrae.get(0));
        generatedParts.add(head);

        return generatedParts;
    }

    private Head generateHead(WholeBody wholeBody, Vector3f boundingBoxScale, SkeletonPart parent) {
        BoundingBox headBox = BoundingBox.defaultBox();
        headBox.scale(boundingBoxScale);

        // we have the world position of the spine and we have to get something that is relative to the parent
        TransformationMatrix headTransform = TransformationMatrix.getInverse(parent.getWorldTransform());
        Point2f spineStartPoint = wholeBody.getGenerator().getSpineLocation().apply(0f);
        Point2f headPosition = new Point2f(spineStartPoint);
        headPosition.sub(new Point2f(headBox.getXLength(), headBox.getYLength() / 2f));
        headTransform.translate(new Vector3f(headPosition.x, headPosition.y, -headBox.getZLength() / 2f));

        Head head = new Head(headTransform, new Point3f(spineStartPoint.x, spineStartPoint.y, 0f), headBox, parent, wholeBody);
        parent.addChild(head);

        return head;
    }

    /**
     * The vertebra are generated from the left side of the interval to the right.
     * If the left float is greater than the right one, then the vertebra are generated in negative direction on the curve.
     * @param interval has to contain two floats between 0 and 1
     * @param vertebraCount number of vertebra that shall be generated (equally spaced)
     * @param firstParent element that shall be parent of the first vertebra generated or a dummy parent from which only the transform is used
     * @param dummyParent indicates if the parent of the first generated vertebra shall be the parent or null
     * @param lastChild if present, the child of the last vertebra generated
     * @return the generated vertebra
     */
    private List<SkeletonPart> generateVertebraInInterval(WholeBody wholeBody, Tuple2f interval, int vertebraCount,
                                                      SkeletonPart firstParent, boolean dummyParent, Optional<SkeletonPart> lastChild) {

        SkeletonPart parent = firstParent;

        BoundingBox boundingBox = BoundingBox.defaultBox();
        Vector3f localBoxTranslation = new Vector3f(0f, -boundingBox.getYLength() / 2f, 0f); // negative half box height
        Vector3f negativeHalfBoxWidth = new Vector3f(0f, 0f, -boundingBox.getZLength() / 2f);
        localBoxTranslation.add(negativeHalfBoxWidth);

        CubicBezierCurve spine = wholeBody.getGenerator().getSpineLocation();

        ArrayList<SkeletonPart> generatedParts = new ArrayList<>();
        float intervalLength = Math.abs(interval.y - interval.x);
        float sign = interval.y > interval.x ? 1f : -1f;

        for (int i = 0; i < vertebraCount; i++) {
            float t = interval.x + sign * (float) i / (float) vertebraCount * intervalLength;
            float tPlus1 = t + sign * 1f / (float) vertebraCount * intervalLength;

            // we have the world position of the spine and we have to get something that is relative to the parent
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
            if (i == 0 && dummyParent) { // this is the real parent (dummy parent was used to calculate it)
                child = new Vertebra(transform, jointRotationPoint, childBox, null, wholeBody); // root
            } else {
                child = new Vertebra(transform, jointRotationPoint, childBox, parent, wholeBody);
                parent.addChild(child);
            }

            // Move child down a negative half bounding box height
            // so that spine is not at the bottom of the vertebra but pierces
            // the bounding box in the center of the left and right side.
            // Do this after the generation of the child to be able to use
            // the world transform method of the child.
            Vector3f transformedBoxTranslation = new Vector3f(localBoxTranslation);
            child.getWorldTransform().applyOnVector(transformedBoxTranslation);
            child.getTransform().translate(transformedBoxTranslation);

            generatedParts.add(child);
            parent = child;
        }

        lastChild.ifPresent(
                skeletonPart -> generatedParts.get(generatedParts.size() - 1).addChild(skeletonPart)
        );

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
