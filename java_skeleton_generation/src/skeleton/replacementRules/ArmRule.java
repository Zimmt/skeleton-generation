package skeleton.replacementRules;

import skeleton.ExtremityData;
import skeleton.elements.SkeletonPart;
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

        Vector3f upperArmScale = new Vector3f(
                0.4f * arm.getParent().getBoundingBox().getXLength(),
                extremityData.getLengthUpperArm(),
                0.3f * arm.getParent().getBoundingBox().getZLength());
        UpperArm upperArm = generateUpperArm(upperArmScale, arm);
        generatedParts.add(upperArm);

        Vector3f lowerArmScale = new Vector3f(
                0.8f * upperArm.getBoundingBox().getXLength(),
                extremityData.getLengthLowerArm(),
                0.8f * upperArm.getBoundingBox().getZLength());
        LowerArm lowerArm = generateLowerArm(lowerArmScale, arm, upperArm);
        generatedParts.add(lowerArm);

        Vector3f handScale = new Vector3f(
                0.8f * lowerArm.getBoundingBox().getXLength(),
                extremityData.getLengthHand(),
                2f * lowerArm.getBoundingBox().getZLength());
        Hand hand = generateHand(handScale, arm, lowerArm);
        generatedParts.add(hand);

        if (extremityData.getFlooredLegs() > 1) {
            findFlooredPosition(arm.getParent(), upperArm, lowerArm, hand, arm.getGenerator().getSkeletonMetaData().getExtremities().getFlooredAnkleWristProbability());
        } else if (extremityData.getWings() > 0) {
            findWingPosition(arm.getParent(), upperArm, lowerArm, hand);
        } else if (extremityData.getArms() >= 1) {
            findArmPosition(arm.getParent(), upperArm, lowerArm, hand);
        } else {
            findFloatingPosition(arm.getParent(), upperArm, lowerArm, hand);
        }
        System.out.println("...finished.");

        return generatedParts;
    }

    private UpperArm generateUpperArm(Vector3f scale, Arm arm) {
        BoundingBox boundingBox = new BoundingBox(scale);
        TransformationMatrix transform = arm.getParent().getJoint().calculateChildTransform(boundingBox);

        UpperArm upperArm = new UpperArm(transform, boundingBox, arm.getParent(), arm, false);
        arm.getParent().replaceChild(arm, upperArm);
        return upperArm;
    }

    private LowerArm generateLowerArm(Vector3f scale, Arm arm, UpperArm upperArm) {
        BoundingBox boundingBox = new BoundingBox(scale);
        TransformationMatrix transform = upperArm.getJoint().calculateChildTransform(boundingBox);

        LowerArm lowerArm = new LowerArm(transform, boundingBox, upperArm, arm, false);
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

    private void findFloatingPosition(Shoulder shoulder, UpperArm upperArm, LowerArm lowerArm, Hand hand) {
        shoulder.getJoint().setCurrentFirstAngle((float) Math.toRadians(90));
        shoulder.getJoint().setCurrentSecondAngle((float) Math.toRadians(90));
        upperArm.setTransform(shoulder.getJoint().calculateChildTransform(upperArm.getBoundingBox()));

        upperArm.getJoint().setCurrentAngle(0f);
        lowerArm.setTransform(upperArm.getJoint().calculateChildTransform(lowerArm.getBoundingBox()));

        lowerArm.getJoint().setCurrentAngle(0f);
        hand.setTransform(lowerArm.getJoint().calculateChildTransform(hand.getBoundingBox()));
    }

    private void findArmPosition(Shoulder shoulder, UpperArm upperArm, LowerArm lowerArm, Hand hand) {
        shoulder.getJoint().setCurrentFirstAngle(0f);
        shoulder.getJoint().setCurrentSecondAngle(0f);
        upperArm.setTransform(shoulder.getJoint().calculateChildTransform(upperArm.getBoundingBox()));

        upperArm.getJoint().setCurrentAngle((float) -Math.toRadians(90));
        lowerArm.setTransform(upperArm.getJoint().calculateChildTransform(lowerArm.getBoundingBox()));

        lowerArm.getJoint().setCurrentAngle(0f);
        hand.setTransform(lowerArm.getJoint().calculateChildTransform(hand.getBoundingBox()));
    }

    private void findWingPosition(Shoulder shoulder, UpperArm upperArm, LowerArm lowerArm, Hand hand) {
        shoulder.getJoint().setRandomWingAngles();
        upperArm.setTransform(shoulder.getJoint().calculateChildTransform(upperArm.getBoundingBox()));

        upperArm.getJoint().setRandomWingAngle();
        lowerArm.setTransform(upperArm.getJoint().calculateChildTransform(lowerArm.getBoundingBox()));

        lowerArm.getJoint().setRandomWingAngle();
        hand.setTransform(lowerArm.getJoint().calculateChildTransform(hand.getBoundingBox()));
    }

    /**
     * Adapts angles of arm until a position is reached where the floor is touched
     * @param flooredWristProbability probability for the wrist to touch the floor (otherwise the tip of the hand will be used)
     * @return if it was successful; reasons for failing could be that
     * - the arm is too short to reach the floor
     * - or the maximum number of steps was exceeded (but angles have already been changed)
     */
    private boolean findFlooredPosition(Shoulder shoulder, UpperArm upperArm, LowerArm lowerArm, Hand hand, float flooredWristProbability) {
        shoulder.getJoint().setChild(upperArm);
        upperArm.getJoint().setChild(lowerArm);
        lowerArm.getJoint().setChild(hand);

        float eps = 1f;
        float angleStepSize = (float) Math.toRadians(30);
        int maxSteps = 40;
        int step = 0;

        float shoulderSideAngleP = 0.5f;
        float shoulderFrontAngleP = 0.3f;
        float upperLowerArmAngleP = 0.7f;
        float handSideAngleP = 0.9f;
        float oppositeDirP = 0f;
        Random random = new Random();
        boolean flooredWrist = random.nextFloat() < flooredWristProbability;
        shoulder.getGenerator().getSkeletonMetaData().getExtremities().setFlooredAnkleWristProbability(flooredWrist); // other extremities do the same
        float floorHeight = shoulder.getGenerator().getSkeletonMetaData().getExtremities().getFloorHeight();

        Point3f endPosition;
        if (flooredWrist) {
            endPosition = hand.getWorldPosition();
        } else {
            endPosition = hand.getWorldPosition();
        }

        while (Math.abs(endPosition.y - floorHeight) > eps && step < maxSteps) {
            //System.out.println("Distance to floor is " + (endPosition.y-floorHeight) + ", angle step size: " + Math.toDegrees(angleStepSize));
            boolean nearerToFloor = endPosition.y-floorHeight > 0;

            if (random.nextFloat() < shoulderSideAngleP && shoulder.getJoint().movementPossible(nearerToFloor, true)) {
                shoulder.getJoint().setNewSecondAngle((random.nextFloat() < oppositeDirP) != nearerToFloor, angleStepSize);
            }
            if (random.nextFloat() < shoulderFrontAngleP && shoulder.getJoint().movementPossible(nearerToFloor, false)) {
                //System.out.println("shoulder front angle");
                shoulder.getJoint().setNewFirstAngle((random.nextFloat() < oppositeDirP) != nearerToFloor, angleStepSize);
            }
            upperArm.setTransform(shoulder.getJoint().calculateChildTransform(upperArm.getBoundingBox()));

            if (random.nextFloat() < upperLowerArmAngleP && upperArm.getJoint().movementPossible(nearerToFloor)) {
                upperArm.getJoint().setNewAngle((random.nextFloat() < oppositeDirP) != nearerToFloor, angleStepSize);
                //System.out.println("new upper lower arm angle: " + Math.toDegrees(upperArm.getJoint().getCurrentAngle()));
            }
            lowerArm.setTransform(upperArm.getJoint().calculateChildTransform(lowerArm.getBoundingBox()));

            if (flooredWrist) {
                endPosition = lowerArm.getWorldPosition();
            } else {
                if (random.nextFloat() < handSideAngleP && lowerArm.getJoint().movementPossible(nearerToFloor)) {
                    lowerArm.getJoint().setNewAngle((random.nextFloat() < oppositeDirP) != nearerToFloor, angleStepSize);
                }
                hand.setTransform(lowerArm.getJoint().calculateChildTransform(hand.getBoundingBox()));

                endPosition = hand.getWorldPosition();
            }

            step++;
            if (Math.toDegrees(angleStepSize) > 1) {
                angleStepSize *= 11f/12f;
            }
        }

        System.out.print("finding floored position needed " + step + " steps");
        //System.out.println("Final distance to floor: " + (endPosition.y-floorHeight));

        if (Math.abs(endPosition.y-floorHeight) < eps) {
            if (flooredWrist) { // adjust hand todo also change angle to world y axis ?
                Vector3f localDir = new Vector3f(0f, 1f, 0f);
                lowerArm.calculateWorldTransform().applyOnVector(localDir);
                float angle = new Vector3f(1f, 0f, 0f).angle(new Vector3f(localDir.x, localDir.y, 0f));
                lowerArm.getJoint().setCurrentAngle(-angle);
                hand.setTransform(lowerArm.getJoint().calculateChildTransform(hand.getBoundingBox()));
            }
            return true;
        } else {
            System.err.println("Could not reach floor");
            return false;
        }
    }
}
