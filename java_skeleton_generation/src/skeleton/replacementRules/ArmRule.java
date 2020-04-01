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

        Vector3f upperArmScale = new Vector3f(
                0.4f * shoulder.getBoundingBox().getXLength(),
                extremityData.getLengthUpperArm(),
                0.3f * shoulder.getBoundingBox().getZLength());
        UpperArm upperArm = generateUpperArm(upperArmScale, arm, shoulder.getJoint(), extremityKind);
        generatedParts.add(upperArm);

        Vector3f lowerArmScale = new Vector3f(
                0.8f * upperArm.getBoundingBox().getXLength(),
                extremityData.getLengthLowerArm(),
                0.8f * upperArm.getBoundingBox().getZLength());
        LowerArm lowerArm = generateLowerArm(lowerArmScale, arm, upperArm, extremityKind);
        generatedParts.add(lowerArm);

        Vector3f handScale = new Vector3f(
                lowerArm.getBoundingBox().getXLength(),
                extremityData.getLengthHand(),
                lowerArm.getBoundingBox().getZLength());
        Hand hand = generateHand(handScale, arm, lowerArm);
        generatedParts.add(hand);

        ExtremityPositioning extremityPositioning = new ExtremityPositioning(
                shoulder.getJoint(), upperArm.getJoint(), lowerArm.getJoint(), upperArm, lowerArm, hand);

        if (extremityKind == ExtremityKind.LEG) {
            boolean flooredWrist = (new Random()).nextFloat() < extremityData.getFlooredAnkleWristProbability();
            System.out.print("floored wrist: " + flooredWrist + "... ");

            // other extremities do the same
            upperArm.getGenerator().getSkeletonMetaData().getExtremities().setFlooredAnkleWristProbability(flooredWrist);

            extremityPositioning.findFlooredPosition(flooredWrist);
        } // else nothing needs to be done, position is determined by joints


        System.out.println("...finished.");

        return generatedParts;
    }

    private UpperArm generateUpperArm(Vector3f scale, Arm arm, ShoulderJoint shoulderJoint, ExtremityKind extremityKind) {
        Shoulder shoulder = arm.getParent();
        BoundingBox boundingBox = new BoundingBox(scale);
        TransformationMatrix transform = shoulderJoint.calculateChildTransform(boundingBox);

        UpperArm upperArm = new UpperArm(transform, boundingBox, shoulder, arm, extremityKind);
        shoulder.replaceChild(arm, upperArm);
        return upperArm;
    }

    private LowerArm generateLowerArm(Vector3f scale, Arm arm, UpperArm upperArm, ExtremityKind extremityKind) {
        BoundingBox boundingBox = new BoundingBox(scale);
        TransformationMatrix transform = upperArm.getJoint().calculateChildTransform(boundingBox);

        LowerArm lowerArm = new LowerArm(transform, boundingBox, upperArm, arm, extremityKind);
        upperArm.addChild(lowerArm);
        return lowerArm;
    }

    private Hand generateHand(Vector3f scale, Arm arm, LowerArm lowerArm) {
        BoundingBox boundingBox = new BoundingBox(scale);
        TransformationMatrix transform = lowerArm.getJoint().calculateChildTransform(boundingBox);

        Hand hand = new Hand(transform, boundingBox, lowerArm, arm);
        lowerArm.addChild(hand);
        return hand;
    }
}
