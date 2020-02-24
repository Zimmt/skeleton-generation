package skeleton.replacementRules;

import skeleton.SpinePart;
import skeleton.SpinePosition;
import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.BackPart;
import skeleton.elements.nonterminal.Leg;
import skeleton.elements.terminal.Pelvic;
import skeleton.elements.terminal.RootVertebra;
import skeleton.elements.terminal.Vertebra;
import util.BoundingBox;
import util.CubicBezierCurve;
import util.TransformationMatrix;

import javax.vecmath.Point2f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generates
 * - non terminal leg
 * - terminal vertebrae between root and pelvic
 * - terminal pelvic
 * - terminal vertebrae on tail
 */
public class BackPartRule extends ReplacementRule {

    private final String inputID = "back part";

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart skeletonPart) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(skeletonPart)) {
            return Arrays.asList(skeletonPart);
        }

        BackPart backPart = (BackPart) skeletonPart;
        RootVertebra rootVertebra = backPart.getParent();
        List<SkeletonPart> generatedParts = new ArrayList<>();

        Vector3f pelvicScale = new Vector3f(30f, 20f, 100f);
        List<Float> pelvicIntervalAndLength = findPelvicIntervalAndLength(backPart.getGenerator().getSkeletonMetaData().getSpine(), pelvicScale.x, 0.1f);;

        Tuple2f backBackInterval = new Point2f(rootVertebra.getBackPartJoint().getSpinePosition(), pelvicIntervalAndLength.get(0));
        Vector3f vertebraScale = new Vector3f(10f, 10f, 10f);
        List<Vertebra> backBack = backPart.getGenerator().generateVertebraeInInterval(backPart, SpinePart.BACK,
                backBackInterval, 10, vertebraScale, rootVertebra, rootVertebra.getBackPartJoint());
        rootVertebra.removeChild(backPart);
        generatedParts.addAll(backBack);

        Pelvic pelvic = generatePelvic(backPart, backBack.get(backBack.size()-1), pelvicScale, pelvicIntervalAndLength);
        generatedParts.add(pelvic);

        Leg leg = new Leg(pelvic, backPart);
        pelvic.addChild(leg);
        generatedParts.add(leg);

        Tuple2f tailInterval = new Point2f(pelvic.getTailJoint().getSpinePosition(), 1f);
        List<Vertebra> tail = backPart.getGenerator().generateVertebraeInInterval(backPart, SpinePart.TAIL,
                tailInterval, 15, vertebraScale, pelvic, pelvic.getTailJoint());
        generatedParts.addAll(tail);

        return generatedParts;
    }

    private Pelvic generatePelvic(BackPart backPart, Vertebra parent, Vector3f scales, List<Float> intervalAndLength) {

        BoundingBox boundingBox = new BoundingBox(new Vector3f(intervalAndLength.get(2), scales.y, scales.z));
        parent.getJoint().setChildSpineEndPosition(intervalAndLength.get(1), SpinePart.TAIL);

        TransformationMatrix transform = parent.getJoint().calculateChildTransform(parent);
        transform.translate(Pelvic.getLocalTranslationFromJoint(boundingBox));

        Pelvic pelvic = new Pelvic(transform, boundingBox, parent, backPart, intervalAndLength.get(1));
        parent.addChild(pelvic);

        return pelvic;
    }

    /**
     * Calculates the maximum spine interval with slope in range of +/- slopeEps.
     * Then positions the returned interval as near as possible to the contact point of back and tail.
     * Changes width of pelvic only if the calculated interval is smaller.
     * @return bezier curve parameter for start point on back spine, parameter for end point on tail spine, width
     */
    private List<Float> findPelvicIntervalAndLength(SpinePosition spinePosition, float wantedWidth, float slopeEps) {
        List<Float> interval = new ArrayList<>(2);

        // find possible space for pelvic
        CubicBezierCurve back = spinePosition.getBack();
        float slope = back.getGradient(1f);
        List<Float> backIntervals = back.getIntervalsByGradientEpsilon(slope, slopeEps);
        if (backIntervals.get(backIntervals.size()-1) != 1f) {
            System.err.println("back intervals are wrong!");
        }
        interval.add(backIntervals.get(backIntervals.size()-2));

        CubicBezierCurve tail = spinePosition.getTail();
        float tailSlope = tail.getGradient(0f);
        List<Float> tailIntervals = tail.getIntervalsByGradientEpsilon(tailSlope, slopeEps);
        if (tailIntervals.get(0) != 0f) {
            System.err.println("tail intervals are wrong!");
        }
        interval.add(tailIntervals.get(1));


        // find actual bounds for pelvic
        // lengths can be calculated like that because curve is nearly a line in the interval
        Point2f backPoint = back.apply(interval.get(0));
        backPoint.sub(back.apply(1f));
        float backLength = new Vector2f(backPoint).length();

        Point2f tailPoint = tail.apply(interval.get(1));
        tailPoint.sub(tail.apply(0f));
        float tailLength = new Vector2f(tailPoint).length();

        interval.add(backLength + tailLength);

        if (backLength + tailLength > wantedWidth) {
            float wantedBackLength = wantedWidth/2f;
            float wantedTailLength = wantedWidth/2f;
            if (backLength < wantedBackLength) {
                float diff = wantedBackLength - backLength;
               wantedTailLength = wantedTailLength + diff;
            } else if (tailLength < wantedTailLength) {
                float diff = wantedTailLength - tailLength;
                wantedBackLength = wantedBackLength + diff;
            } // no other case as the length of the interval has enough space for whole length (else initial interval is returned)

            // length = k * associated bezier curve parameter (as curve is nearly straight)
            // use k to get bezier curve parameter for new wanted length
            // new parameter = wanted length / k; k = length / old parameter
            // or in inverted direction:
            // new parameter = 1 - (wanted length / k); k = length / (1 - old parameter)
            interval.set(0, 1f - (wantedBackLength * (1-interval.get(0)) / backLength));
            interval.set(1, wantedTailLength * interval.get(1) / tailLength);
            interval.set(2, wantedWidth);
        }
        return interval;
    }
}
