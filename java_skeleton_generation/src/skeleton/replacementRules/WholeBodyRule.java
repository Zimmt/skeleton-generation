package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.BackPart;
import skeleton.elements.nonterminal.FrontPart;
import skeleton.elements.nonterminal.ShoulderGirdle;
import skeleton.elements.nonterminal.WholeBody;
import skeleton.elements.terminal.Head;
import skeleton.elements.terminal.TerminalElement;
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

        // find positions for shoulder and pelvic on spine
        List<Float> intervals = wholeBody.getGenerator().getSpineLocation().getIntervalsByGradientEpsilon(0.5f);
        System.out.println("spine intervals: " + intervals);

        Tuple2f shoulderSpineInterval = null;
        Tuple2f pelvicSpineInterval = null;
        if (intervals.isEmpty()) { // this should not happen
            shoulderSpineInterval = new Point2f(1f/6f, 2f/6f);
            pelvicSpineInterval = new Point2f(4f/6f, 5f/6f);
            System.out.println("no appropriate spine interval found");

        } else if (intervals.size() == 2) {
            float intervalLength = intervals.get(1) - intervals.get(0);
            shoulderSpineInterval = new Point2f(intervals.get(0), intervals.get(0) + intervalLength / 3f);
            pelvicSpineInterval = new Point2f(intervals.get(0) + 2*intervalLength/3f, intervals.get(1));

        } else { // use fist and last interval TODO better rule?
            shoulderSpineInterval = new Point2f(intervals.get(0), intervals.get(1));
            pelvicSpineInterval = new Point2f(intervals.get(intervals.size()-2), intervals.get(intervals.size()-1));
        }
        System.out.println("shoulder interval: " + shoulderSpineInterval);
        System.out.println("pelvic interval: " + pelvicSpineInterval);

        List<SkeletonPart> generatedParts = new ArrayList<>();

        // spine between shoulder and pelvic (with root vertebra)
        Tuple2f spineInterval = new Point2f(shoulderSpineInterval.y, pelvicSpineInterval.x);
        Vertebra dummyParent = new Vertebra(new TransformationMatrix(), new Point3f(), BoundingBox.defaultBox(), null, wholeBody); // dummy parent
        List<TerminalElement> middleVertebrae = wholeBody.getGenerator().generateVertebraInInterval(wholeBody, spineInterval, 10,
                dummyParent, true, Optional.empty());

        generatedParts.addAll(middleVertebrae);

        // front part
        FrontPart frontPart = generateFrontPart(wholeBody, shoulderSpineInterval, middleVertebrae.get(0));
        generatedParts.add(frontPart);

        // back part
        BackPart backPart = generateBackPart(wholeBody, pelvicSpineInterval, middleVertebrae.get(middleVertebrae.size()-1));
        generatedParts.add(backPart);

        return generatedParts;
    }

    private FrontPart generateFrontPart(WholeBody wholeBody, Tuple2f shoulderSpineInterval, TerminalElement parent) {

        // the position of the front part is simply the end of the shoulder spine interval
        TransformationMatrix transform = TransformationMatrix.getInverse(parent.getWorldTransform());
        Point3f position = wholeBody.getGenerator().getSpineLocation().apply3d(shoulderSpineInterval.y);
        transform.translate(new Vector3f(position));

        Point3f jointRotationPoint = new Point3f(0f, parent.getBoundingBox().getYLength() / 2f, 0f);

        FrontPart frontPart = new FrontPart(transform, jointRotationPoint, parent, wholeBody, shoulderSpineInterval);
        parent.addChild(frontPart);

        return frontPart;
    }

    private BackPart generateBackPart(WholeBody wholeBody, Tuple2f pelvicSpineInterval, TerminalElement parent) {

        // the position of the back part is simply the beginning of the pelvic spine interval
        TransformationMatrix transform = TransformationMatrix.getInverse(parent.getWorldTransform());
        Point3f position = wholeBody.getGenerator().getSpineLocation().apply3d(pelvicSpineInterval.x);
        transform.translate(new Vector3f(position));

        Point3f jointRotationPoint = new Point3f(parent.getBoundingBox().getXLength(), parent.getBoundingBox().getYLength() / 2f, 0f);

        BackPart backPart = new BackPart(transform, jointRotationPoint, parent, wholeBody, pelvicSpineInterval);
        parent.addChild(backPart);

        return backPart;
    }

    /*private Head generateHead(WholeBody wholeBody, Vector3f boundingBoxScale, TerminalElement parent) {
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
    }*/
}
