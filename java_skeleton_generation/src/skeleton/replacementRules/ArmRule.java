package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;
import skeleton.elements.joints.DummyJoint;
import skeleton.elements.nonterminal.Arm;
import skeleton.elements.terminal.Hand;
import skeleton.elements.terminal.LowerArm;
import skeleton.elements.terminal.Shoulder;
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

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(scale);

        TransformationMatrix transform = ((Shoulder) arm.getParent()).getJoint().calculateChildTransform(arm.getParent()); // todo
        transform.translate(new Vector3f(boundingBox.getXLength()/2f, -boundingBox.getYLength(), boundingBox.getZLength()/2f));

        Point3f jointPosition = new Point3f(boundingBox.getXLength()/2f,0f, boundingBox.getZLength()/2f);
        DummyJoint joint = new DummyJoint(jointPosition);

        UpperArm upperArm = new UpperArm(transform, boundingBox, arm.getParent(), arm, joint);
        arm.getParent().replaceChild(arm, upperArm);

        return upperArm;
    }

    private LowerArm generateLowerArm(Vector3f scale, Arm arm, UpperArm upperArm) {

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(scale);

        TransformationMatrix transform = upperArm.getJoint().calculateChildTransform(upperArm);
        transform.translate(new Vector3f(boundingBox.getXLength()/2f, -boundingBox.getYLength(), boundingBox.getZLength()/2f));

        Point3f jointPosition = new Point3f(boundingBox.getXLength()/2f, 0f, boundingBox.getZLength()/2f);
        DummyJoint joint = new DummyJoint(jointPosition);

        LowerArm lowerArm = new LowerArm(transform, boundingBox, upperArm, arm, joint);
        upperArm.addChild(lowerArm);

        return lowerArm;
    }

    private Hand generateHand(Vector3f scale, Arm arm, LowerArm lowerArm) {

        BoundingBox boundingBox = BoundingBox.defaultBox();
        boundingBox.scale(scale);

        TransformationMatrix transform = lowerArm.getJoint().calculateChildTransform(lowerArm);
        transform.translate(new Vector3f(-boundingBox.getXLength(), -boundingBox.getYLength(), boundingBox.getZLength()/2f));


        Hand hand = new Hand(transform, boundingBox, lowerArm, arm);
        lowerArm.addChild(hand);

        return hand;
    }
}
