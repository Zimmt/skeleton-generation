package skeleton.replacementRules;

import skeleton.SpinePart;
import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.FrontPart;
import skeleton.elements.nonterminal.ShoulderGirdle;
import skeleton.elements.terminal.Head;
import skeleton.elements.terminal.TerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generates
 * - non terminal shoulder girdle
 * - terminal vertebrae between root and shoulder girdle TODO: add chest
 * - terminal vertebra that is parent of shoulder girdle
 * - terminal vertebrae of neck
 * - terminal head
 */
public class FrontPartRule extends ReplacementRule {

    private final String inputID = "front part";

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart skeletonPart) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(skeletonPart)) {
            return Arrays.asList(skeletonPart);
        }

        FrontPart frontPart = (FrontPart) skeletonPart;
        List<SkeletonPart> generatedParts = new ArrayList<>();

        Tuple2f frontBackInterval = new Point2f(frontPart.getEndPosition(), 0f);
        Vector3f vertebraScale = new Vector3f(10f, 10f, 10f);
        List<TerminalElement> frontBack = frontPart.getGenerator().generateVertebraeInInterval(frontPart, SpinePart.BACK,
                frontBackInterval, 10, vertebraScale, frontPart.getParent(), false);
        frontPart.getParent().removeChild(frontPart);
        generatedParts.addAll(frontBack);

        TerminalElement shoulderVertebra = frontBack.get(frontBack.size() - 1);
        ShoulderGirdle shoulderGirdle = generateShoulderGirdle(frontPart, shoulderVertebra);
        generatedParts.add(shoulderGirdle);

        Tuple2f neckInterval = new Point2f(1f, 0f);
        List<TerminalElement> neck = frontPart.getGenerator().generateVertebraeInInterval(frontPart, SpinePart.NECK,
                neckInterval, 10, vertebraScale, shoulderVertebra, false);
        generatedParts.addAll(neck);

        Head head = generateHead(frontPart, new Vector3f(50f, 25f, 30f), neck.get(neck.size() - 1));
        generatedParts.add(head);

        return generatedParts;
    }

    /**
     * position: same as shoulder vertebra (parent)
     * joint rotation point: front side of parent in the middle
     */
    private ShoulderGirdle generateShoulderGirdle(FrontPart frontPart, TerminalElement parent) {

        TransformationMatrix transform = new TransformationMatrix();
        Point3f jointRotationPoint = new Point3f(parent.getBoundingBox().getXLength() / 2, parent.getBoundingBox().getYLength() / 2, 0f);

        ShoulderGirdle shoulderGirdle = new ShoulderGirdle(transform, jointRotationPoint, parent, frontPart);
        parent.addChild(shoulderGirdle);

        return shoulderGirdle;
    }

    /**
     * position: middle of right side is the end point of the neck part of the spine
     * joint rotation point: middle of the right side
     */
    private Head generateHead(FrontPart frontPart, Vector3f boundingBoxScale, TerminalElement parent) {
        BoundingBox headBox = BoundingBox.defaultBox();
        headBox.scale(boundingBoxScale);

        Point3f neckStartPoint = frontPart.getGenerator().getSkeletonMetaData().getSpine().getNeck().apply3d(0f);
        Point3f globalHeadPosition = new Point3f(neckStartPoint);
        globalHeadPosition.sub(new Point3f(headBox.getXLength(), headBox.getYLength() / 2f, headBox.getZLength() / 2f));

        TransformationMatrix headTransform = TransformationMatrix.getInverse(parent.calculateWorldTransform());
        headTransform.translate(new Vector3f(globalHeadPosition));

        Point3f jointRotationPoint = new Point3f(headBox.getXLength(), headBox.getYLength() / 2f, headBox.getZLength() / 2f);

        Head head = new Head(headTransform, jointRotationPoint, headBox, parent, frontPart);
        parent.addChild(head);

        return head;
    }
}
