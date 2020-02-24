package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.Arm;
import skeleton.elements.terminal.Hand;
import skeleton.elements.terminal.LowerArm;
import skeleton.elements.terminal.UpperArm;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generates
 * - terminal upper arm
 * - terminal lower arm
 * - terminal hand
 */
public class ArmRule extends ReplacementRule {

    private final String inputID = "arm";

    public String getInputID() {return inputID; }

    public List<SkeletonPart> apply(SkeletonPart skeletonPart) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(skeletonPart)) {
            return Arrays.asList(skeletonPart);
        }

        Arm arm = (Arm) skeletonPart;
        List<SkeletonPart> generatedParts = new ArrayList<>();

        float upperLowerArmRate = 2f / 3f;
        float handHeight = 10f;

        // todo hand and lower arm only touch when arm is vertical
        Point3f parentPosition = new Point3f(arm.getParent().getWorldPosition());
        float upperArmHeight = (parentPosition.y - handHeight) * upperLowerArmRate;
        float lowerArmHeight = parentPosition.y - handHeight - upperArmHeight;

        Vector3f upperArmScale = new Vector3f(
                0.6f * arm.getParent().getBoundingBox().getXLength(),
                upperArmHeight,
                0.4f * arm.getParent().getBoundingBox().getZLength());
        UpperArm upperArm = generateUpperArm(upperArmScale, arm);
        generatedParts.add(upperArm);

        Vector3f lowerArmScale = new Vector3f(
                0.8f * upperArm.getBoundingBox().getXLength(),
                lowerArmHeight,
                0.8f * upperArm.getBoundingBox().getZLength());
        LowerArm lowerArm = generateLowerArm(lowerArmScale, arm, upperArm);
        generatedParts.add(lowerArm);

        Vector3f handScale = new Vector3f(
                4f * lowerArm.getBoundingBox().getXLength(),
                handHeight,
                2f * lowerArm.getBoundingBox().getZLength());
        Hand hand = generateHand(handScale, arm, lowerArm);
        generatedParts.add(hand);

        return generatedParts;
    }

    private UpperArm generateUpperArm(Vector3f scale, Arm arm) {

        BoundingBox boundingBox = new BoundingBox(scale);

        TransformationMatrix transform = arm.getParent().getJoint().calculateChildTransform(arm.getParent());
        transform.translate(UpperArm.getLocalTranslationFromJoint(boundingBox));

        UpperArm upperArm = new UpperArm(transform, boundingBox, arm.getParent(), arm, false);
        arm.getParent().replaceChild(arm, upperArm);

        return upperArm;
    }

    private LowerArm generateLowerArm(Vector3f scale, Arm arm, UpperArm upperArm) {

        BoundingBox boundingBox = new BoundingBox(scale);

        TransformationMatrix transform = upperArm.getJoint().calculateChildTransform(upperArm);
        transform.translate(LowerArm.getLocalTranslationFromJoint(boundingBox));

        LowerArm lowerArm = new LowerArm(transform, boundingBox, upperArm, arm, false);
        upperArm.addChild(lowerArm);

        return lowerArm;
    }

    private Hand generateHand(Vector3f scale, Arm arm, LowerArm lowerArm) {

        BoundingBox boundingBox = new BoundingBox(scale);

        TransformationMatrix transform = lowerArm.getJoint().calculateChildTransform(lowerArm);
        transform.translate(Hand.getLocalTranslationFromJoint(boundingBox));

        Hand hand = new Hand(transform, boundingBox, lowerArm, arm);
        lowerArm.addChild(hand);

        return hand;
    }
}
