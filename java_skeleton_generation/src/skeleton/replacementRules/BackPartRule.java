package skeleton.replacementRules;

import skeleton.SpinePart;
import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.BackPart;
import skeleton.elements.nonterminal.Leg;
import skeleton.elements.terminal.Pelvic;
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
 * - terminal pelvic
 * - non terminal leg
 * - terminal vertebrae on spine for tail
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
        List<SkeletonPart> generatedParts = new ArrayList<>();

        Pelvic pelvic = generatePelvic(backPart, 4f, 1.5f);
        generatedParts.add(pelvic);

        Leg leg = generateLeg(backPart, pelvic);
        generatedParts.add(leg);

        Tuple2f tailInterval = new Point2f(backPart.getPelvicSpineInterval().y, 1f);
        List<TerminalElement> tail = backPart.getGenerator().generateVertebraeInInterval(backPart, SpinePart.BACK, tailInterval, 3, pelvic, false);
        generatedParts.addAll(tail);

        return generatedParts;
    }

    private Pelvic generatePelvic(BackPart backPart, float width, float height) {

        Tuple2f spineInterval = backPart.getPelvicSpineInterval();
        Point2f leftSpinePoint = backPart.getGenerator().getSpinePosition().apply(spineInterval.x);
        Point2f rightSpinePoint = backPart.getGenerator().getSpinePosition().apply(spineInterval.y);
        float xLength = Math.abs(rightSpinePoint.x - leftSpinePoint.x);

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(new Vector3f(xLength, height, width));

        TerminalElement parent = backPart.getParent();
        Point3f jointRotationPoint = new Point3f(0f, parent.getBoundingBox().getYLength() / 2f, parent.getBoundingBox().getZLength() / 2f);

        TransformationMatrix transform = backPart.getGenerator().generateTransformForElementInSpineInterval(spineInterval, parent);
        Vector3f translation = new Vector3f(0f, -boundingBox.getYLength() / 2f, -boundingBox.getZLength() / 2f);

        Pelvic pelvic = new Pelvic(transform, jointRotationPoint, boundingBox, parent, backPart);
        pelvic.calculateWorldTransform().applyOnVector(translation);
        pelvic.getTransform().translate(translation); // translate down and back half box height in world coordinates

        parent.replaceChild(backPart, pelvic);

        return pelvic;
    }

    /**
     * The position of the leg is the same as the position of the pelvic
     */
    private Leg generateLeg(BackPart backPart, Pelvic pelvic) {

        TransformationMatrix transform = new TransformationMatrix();
        Point3f jointRotationPoint = new Point3f(pelvic.getBoundingBox().getXLength() / 2f, 0f, 0f);

        Leg leg = new Leg(transform, jointRotationPoint, pelvic, backPart);
        pelvic.addChild(leg);

        return leg;
    }
}
