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
        List<SkeletonPart> generatedParts = new ArrayList<>();

        Tuple2f backBackInterval = new Point2f(backPart.getStartPosition(), 1f);
        Vector3f vertebraScale = new Vector3f(10f, 10f, 10f);
        List<TerminalElement> backBack = backPart.getGenerator().generateVertebraeInInterval(backPart, SpinePart.BACK,
                backBackInterval, 10, vertebraScale, backPart.getParent(), false);
        backPart.getParent().removeChild(backPart);
        generatedParts.addAll(backBack);

        Pelvic pelvic = generatePelvic(backPart, backBack.get(backBack.size()-1),new Vector3f(30f, 20f, 100f));
        generatedParts.add(pelvic);

        Leg leg = generateLeg(backPart, pelvic);
        generatedParts.add(leg);

        Tuple2f tailInterval = new Point2f(0f, 1f);
        List<TerminalElement> tail = backPart.getGenerator().generateVertebraeInInterval(backPart, SpinePart.TAIL,
                tailInterval, 15, vertebraScale, pelvic, false);
        generatedParts.addAll(tail);

        return generatedParts;
    }

    /**
     * position: the last control point of the back spine is in the middle of the pelvic
     * joint rotation point: right side of parent in the middle
     */
    private Pelvic generatePelvic(BackPart backPart, TerminalElement parent, Vector3f scales) {

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(scales);

        Point3f jointRotationPoint = new Point3f(parent.getBoundingBox().getXLength(), parent.getBoundingBox().getYLength() / 2f, parent.getBoundingBox().getZLength() / 2f);

        Point2f twoDPosition = backPart.getGenerator().getSkeletonMetaData().getSpine().getBack().getControlPoint3();
        Point3f worldPosition = new Point3f(twoDPosition.x, twoDPosition.y, 0f);
        worldPosition.sub(new Point3f(boundingBox.getXLength() / 2f, boundingBox.getYLength() / 2f, boundingBox.getZLength() / 2f));

        TransformationMatrix transform = TransformationMatrix.getInverse(parent.calculateWorldTransform());
        transform.translate(new Vector3f(worldPosition));

        Pelvic pelvic = new Pelvic(transform, jointRotationPoint, boundingBox, parent, backPart);
        parent.addChild(pelvic);

        return pelvic;
    }

    /**
     * position: same as pelvic
     * joint rotation point: front side of pelvic bottom border in the middle
     */
    private Leg generateLeg(BackPart backPart, Pelvic pelvic) {

        TransformationMatrix transform = new TransformationMatrix();
        Point3f jointRotationPoint = new Point3f(pelvic.getBoundingBox().getXLength() / 2f, 0f, 0f);

        Leg leg = new Leg(transform, jointRotationPoint, pelvic, backPart);
        pelvic.addChild(leg);

        return leg;
    }
}
