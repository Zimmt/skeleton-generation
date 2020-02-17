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

        Tuple2f neckInterval = new Point2f(frontPart.getFrontPartStartPosition(), 0f);
        List<TerminalElement> neck = frontPart.getGenerator().generateVertebraeInInterval(frontPart, SpinePart.NECK, neckInterval, 3, frontPart.getParent(), false);
        frontPart.getParent().removeChild(frontPart);
        generatedParts.addAll(neck);

        TerminalElement shoulderVertebra = neck.get(0);
        ShoulderGirdle shoulderGirdle = generateShoulderGirdle(frontPart, shoulderVertebra);
        generatedParts.add(shoulderGirdle);

        Head head = generateHead(frontPart, new Vector3f(2f, 1f, 1.5f), neck.get(neck.size()-1));
        generatedParts.add(head);

        return generatedParts;
    }

    private ShoulderGirdle generateShoulderGirdle(FrontPart frontPart, TerminalElement parent) {

        // the position of the shoulder girdle is the same as the position of the shoulder vertebra (parent element)
        TransformationMatrix transform = new TransformationMatrix();
        Point3f jointRotationPoint = new Point3f(parent.getBoundingBox().getXLength() / 2, parent.getBoundingBox().getYLength() / 2, 0f);

        ShoulderGirdle shoulderGirdle = new ShoulderGirdle(transform, jointRotationPoint, parent, frontPart);
        parent.addChild(shoulderGirdle);

        return shoulderGirdle;
    }

    private Head generateHead(FrontPart frontPart, Vector3f boundingBoxScale, TerminalElement parent) {
        BoundingBox headBox = BoundingBox.defaultBox();
        headBox.scale(boundingBoxScale);

        // we have the world position of the spine and we have to get something that is relative to the parent
        TransformationMatrix headTransform = TransformationMatrix.getInverse(parent.calculateWorldTransform());
        Point2f spineStartPoint = frontPart.getGenerator().getSpinePosition().apply(0f);
        Point2f headPosition = new Point2f(spineStartPoint);
        headPosition.sub(new Point2f(headBox.getXLength(), headBox.getYLength() / 2f));
        headTransform.translate(new Vector3f(headPosition.x, headPosition.y, -headBox.getZLength() / 2f));

        Head head = new Head(headTransform, new Point3f(spineStartPoint.x, spineStartPoint.y, 0f), headBox, parent, frontPart);
        parent.addChild(head);

        return head;
    }
}
