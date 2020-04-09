package skeleton.replacementRules;

import skeleton.elements.ExtremityKind;
import skeleton.elements.SkeletonPart;
import skeleton.elements.joints.arm.ShoulderJoint;
import skeleton.elements.nonterminal.Arm;
import skeleton.elements.terminal.Hand;
import skeleton.elements.terminal.LowerArm;
import skeleton.elements.terminal.Shoulder;
import skeleton.elements.terminal.UpperArm;
import util.BoundingBox;
import util.TransformationMatrix;

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

    public String getInputID() { return inputID; }

    public List<SkeletonPart> apply(SkeletonPart skeletonPart) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(skeletonPart)) {
            return Arrays.asList(skeletonPart);
        }
        //System.out.print("Arm generation... ");

        Arm arm = (Arm) skeletonPart;
        List<SkeletonPart> generatedParts = new ArrayList<>();
        Shoulder shoulder = arm.getParent();

        ExtremityPositioning extremityPositioning = shoulder.getJoint().getExtremityPositioning();

        UpperArm upperArm = generateUpperArm(arm, shoulder.getJoint(), extremityPositioning.getExtremityKind());
        generatedParts.add(upperArm);

        LowerArm lowerArm = generateLowerArm(arm, upperArm, extremityPositioning.getExtremityKind());
        generatedParts.add(lowerArm);

        Hand hand = generateHand(arm, lowerArm);
        generatedParts.add(hand);

        extremityPositioning.setBonesAndJoints(shoulder.getJoint(), upperArm.getJoint(), lowerArm.getJoint(), upperArm, lowerArm, hand);
        extremityPositioning.findPosition();

        //System.out.println("...finished.");

        return generatedParts;
    }

    private UpperArm generateUpperArm(Arm arm, ShoulderJoint shoulderJoint, ExtremityKind extremityKind) {
        Shoulder shoulder = arm.getParent();
        float xzScale = 0.2f * shoulder.getBoundingBox().getXLength();
        BoundingBox boundingBox = new BoundingBox(new Vector3f(
                xzScale,
                arm.getGenerator().getSkeletonMetaData().getExtremities().getLengthUpperArm(),
                xzScale));
        TransformationMatrix transform = shoulderJoint.calculateChildTransform(boundingBox);

        UpperArm upperArm = new UpperArm(transform, boundingBox, shoulder, arm, extremityKind);
        shoulder.replaceChild(arm, upperArm);
        return upperArm;
    }

    private LowerArm generateLowerArm(Arm arm, UpperArm upperArm, ExtremityKind extremityKind) {
        float xzScale = upperArm.getBoundingBox().getXLength();
        BoundingBox boundingBox = new BoundingBox(new Vector3f(
                xzScale,
                arm.getGenerator().getSkeletonMetaData().getExtremities().getLengthLowerArm(),
                xzScale));
        TransformationMatrix transform = upperArm.getJoint().calculateChildTransform(boundingBox);

        LowerArm lowerArm = new LowerArm(transform, boundingBox, upperArm, arm, extremityKind);
        upperArm.addChild(lowerArm);
        return lowerArm;
    }

    private Hand generateHand(Arm arm, LowerArm lowerArm) {
        float xzScale = 0.7f * lowerArm.getBoundingBox().getXLength();
        BoundingBox boundingBox = new BoundingBox(new Vector3f(
                xzScale,
                lowerArm.getGenerator().getSkeletonMetaData().getExtremities().getLengthHand(),
                xzScale));
        TransformationMatrix transform = lowerArm.getJoint().calculateChildTransform(boundingBox);

        Hand hand = new Hand(transform, boundingBox, lowerArm, arm);
        lowerArm.addChild(hand);
        return hand;
    }
}
