package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.FrontPart;
import skeleton.elements.terminal.Head;
import skeleton.elements.terminal.Shoulder;
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
 * - terminal shoulder (TODO Arm still missing)
 * - terminal vertebrae on spine between shoulders and head
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

        Shoulder shoulder = generateShoulder(frontPart, 2f, 1.5f);
        generatedParts.add(shoulder);

        Tuple2f neckInterval = new Point2f(0f, frontPart.getShoulderSpineInterval().x);
        List<TerminalElement> neck = frontPart.getGenerator().generateVertebraeInInterval(frontPart, neckInterval, 3, shoulder, false);
        generatedParts.addAll(neck);

        Head head = generateHead(frontPart, new Vector3f(2f, 1f, 1.5f), neck.get(neck.size()-1));
        generatedParts.add(head);

        return generatedParts;
    }

    private Shoulder generateShoulder(FrontPart frontPart, float width, float height) {

        Tuple2f spineInterval = frontPart.getShoulderSpineInterval();
        Point2f leftSpinePoint = frontPart.getGenerator().getSpine().apply(spineInterval.x);
        Point2f rightSpinePoint = frontPart.getGenerator().getSpine().apply(spineInterval.y);
        float xLength = Math.abs(rightSpinePoint.x - leftSpinePoint.x);

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(new Vector3f(xLength, height, width));

        TerminalElement parent = frontPart.getParent();
        Point3f jointRotationPoint = new Point3f(0f, parent.getBoundingBox().getYLength() / 2f, parent.getBoundingBox().getZLength() / 2f);

        TransformationMatrix transform = frontPart.getGenerator().generateTransformForElementInSpineInterval(spineInterval, parent);
        Vector3f translation = new Vector3f(0f, -boundingBox.getYLength() / 2f, 0f);

        Shoulder shoulder = new Shoulder(transform, jointRotationPoint, boundingBox, parent, frontPart);
        shoulder.calculateWorldTransform().applyOnVector(translation);
        shoulder.getTransform().translate(translation); // translate down half box height in world coordinates

        parent.replaceChild(frontPart, shoulder);

        return shoulder;
    }

    private Head generateHead(FrontPart frontPart, Vector3f boundingBoxScale, TerminalElement parent) {
        BoundingBox headBox = BoundingBox.defaultBox();
        headBox.scale(boundingBoxScale);

        // we have the world position of the spine and we have to get something that is relative to the parent
        TransformationMatrix headTransform = TransformationMatrix.getInverse(parent.calculateWorldTransform());
        Point2f spineStartPoint = frontPart.getGenerator().getSpine().apply(0f);
        Point2f headPosition = new Point2f(spineStartPoint);
        headPosition.sub(new Point2f(headBox.getXLength(), headBox.getYLength() / 2f));
        headTransform.translate(new Vector3f(headPosition.x, headPosition.y, -headBox.getZLength() / 2f));

        Head head = new Head(headTransform, new Point3f(spineStartPoint.x, spineStartPoint.y, 0f), headBox, parent, frontPart);
        parent.addChild(head);

        return head;
    }
}
