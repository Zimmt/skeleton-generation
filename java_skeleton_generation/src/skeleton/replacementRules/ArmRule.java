package skeleton.replacementRules;

import skeleton.ExtremityData;
import skeleton.elements.SkeletonPart;
import skeleton.elements.ExtremityKind;
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
import java.util.Random;

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
        System.out.print("Arm generation... ");

        Arm arm = (Arm) skeletonPart;
        List<SkeletonPart> generatedParts = new ArrayList<>();
        ExtremityData extremityData = arm.getGenerator().getSkeletonMetaData().getExtremities();
        Shoulder shoulder = arm.getParent();

        ExtremityKind extremityKind = shoulder.getJoint().getExtremityKind();

        UpperArm upperArm = generateUpperArm(arm, shoulder.getJoint(), extremityKind);
        generatedParts.add(upperArm);

        LowerArm lowerArm = generateLowerArm(arm, upperArm, extremityKind);
        generatedParts.add(lowerArm);

        Hand hand = generateHand(arm, lowerArm);
        generatedParts.add(hand);

        ExtremityPositioning extremityPositioning = new ExtremityPositioning(
                shoulder.getJoint(), upperArm.getJoint(), lowerArm.getJoint(), upperArm, lowerArm, hand);

        if (extremityKind == ExtremityKind.LEG) {
            boolean flooredWrist = (new Random()).nextFloat() < extremityData.getFlooredAnkleWristProbability();
            System.out.print("floored wrist: " + flooredWrist + "... ");

            // other extremities do the same
            extremityData.setFlooredAnkleWristProbability(flooredWrist);

            extremityPositioning.findFlooredPosition(flooredWrist);
        } // else nothing needs to be done, position is determined by joints


        System.out.println("...finished.");

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
