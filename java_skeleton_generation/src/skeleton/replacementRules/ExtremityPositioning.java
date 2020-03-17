package skeleton.replacementRules;

import skeleton.elements.joints.OneAngleBasedJoint;
import skeleton.elements.joints.XZAngleBasedJoint;
import skeleton.elements.terminal.*;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.Random;

public class ExtremityPositioning {

    private XZAngleBasedJoint firstJoint; // joint between parent of fist bone and first bone
    private OneAngleBasedJoint secondJoint; // joint between first and second bone
    private OneAngleBasedJoint thirdJoint; // joint between second and third bone

    private TerminalElement firstBone;
    private TerminalElement secondBone;
    private TerminalElement thirdBone;

    // these values are only used to find floored position
    private float floorDistanceEps = 1f;
    private float initialAngleStepSize = (float) Math.toRadians(20);
    private int maxSteps = 40;
    private float firstJointXAngleProbability = 0.8f;
    private float firstJointZAngleProbability = 1f;
    private float secondJointAngleProbability = 1f;
    private float thirdJointAngleProbability = 1f;

    private Random random = new Random();

    public ExtremityPositioning(XZAngleBasedJoint firstJoint, OneAngleBasedJoint secondJoint, OneAngleBasedJoint thirdJoint,
                                TerminalElement firstBone, TerminalElement secondBone, TerminalElement thirdBone) {
        this.firstJoint = firstJoint;
        this.secondJoint = secondJoint;
        this.thirdJoint = thirdJoint;
        this.firstBone = firstBone;
        this.secondBone = secondBone;
        this.thirdBone = thirdBone;
    }

    /**
     * sets the following angles:
     * - first joint: both angles 90°
     * - second joint: 0°
     * - third joint: 0°
     */
    public void findFloatingPosition() {
        firstJoint.setCurrentFirstAngle((float) Math.toRadians(90));
        firstJoint.setCurrentSecondAngle((float) Math.toRadians(90));
        firstBone.setTransform(firstJoint.calculateChildTransform(firstBone.getBoundingBox()));

        secondJoint.setCurrentAngle(0f);
        secondBone.setTransform(secondJoint.calculateChildTransform(secondBone.getBoundingBox()));

        thirdJoint.setCurrentAngle(0f);
        thirdBone.setTransform(thirdJoint.calculateChildTransform(thirdBone.getBoundingBox()));
    }

    /**
     * sets the following angles:
     * - first joint: both angles 0°
     * - second joint: -90°
     * - third joint: 0°
     */
    public void findArmPosition() {
        firstJoint.setCurrentFirstAngle(0f);
        firstJoint.setCurrentSecondAngle(0f);
        firstBone.setTransform(firstJoint.calculateChildTransform(firstBone.getBoundingBox()));

        secondJoint.setCurrentAngle((float) -Math.toRadians(90));
        secondBone.setTransform(secondJoint.calculateChildTransform(secondBone.getBoundingBox()));

        thirdJoint.setCurrentAngle(0f);
        thirdBone.setTransform(thirdJoint.calculateChildTransform(thirdBone.getBoundingBox()));
    }

    /**
     * sets random angles
     */
    public void findWingPosition() {
        firstJoint.setRandomAngles();
        firstBone.setTransform(firstJoint.calculateChildTransform(firstBone.getBoundingBox()));

        secondJoint.setRandomAngle();
        secondBone.setTransform(secondJoint.calculateChildTransform(secondBone.getBoundingBox()));

        thirdJoint.setRandomAngle();
        thirdBone.setTransform(thirdJoint.calculateChildTransform(thirdBone.getBoundingBox()));
    }

    /**
     * Adapts angles of joints until a position is reached where the floor is touched
     * @param flooredSecondBone if end of second bone should touch the floor (otherwise the third bone will be used)
     * @return if it was successful; reasons for failing could be that
     * - the bones are too short to reach the floor
     * - or the maximum number of steps was exceeded (but angles have already been changed)
     */
    public boolean findFlooredPosition(boolean flooredSecondBone) {
        firstJoint.setChild(firstBone);
        secondJoint.setChild(secondBone);
        thirdJoint.setChild(thirdBone);

        int step = 0;
        float angleStepSize = initialAngleStepSize;
        float floorHeight = firstBone.getGenerator().getSkeletonMetaData().getExtremities().getFloorHeight();

        Point3f firstBoneEndPosition = firstBone.getWorldPosition();
        Point3f secondBoneEndPosition = secondBone.getWorldPosition();
        Point3f thirdBoneEndPosition = thirdBone.getWorldPosition();

        if (firstBoneEndPosition.y < floorHeight-floorDistanceEps || secondBoneEndPosition.y < floorHeight-floorDistanceEps || thirdBoneEndPosition.y < floorHeight-floorDistanceEps ) {
            System.err.println("Other start position needed!");
            return false;
        }

        Point3f endPosition;
        if (flooredSecondBone) {
            endPosition = secondBoneEndPosition;
        } else {
            endPosition = thirdBoneEndPosition;
        }

        while (Math.abs(endPosition.y - floorHeight) > floorDistanceEps && step < maxSteps) {
            boolean nearerToFloor = true;
            float oldDistance = endPosition.y - floorHeight;
            System.out.println("Distance to floor: " + oldDistance);

            if (firstBoneEndPosition.y > floorHeight+floorDistanceEps &&
                    random.nextFloat() < firstJointXAngleProbability && firstJoint.movementPossible(nearerToFloor, true)) {
                firstJoint.setNewSecondAngle(nearerToFloor, angleStepSize);
                firstBoneEndPosition = firstBoneSetTransformAndCalculateWorldPosition();
                if (!bonePositionsOverFloor(floorHeight)) {
                    firstJoint.resetCurrentSecondAngle();
                    firstBoneEndPosition = firstBoneSetTransformAndCalculateWorldPosition();
                    if (!bonePositionsOverFloor(floorHeight)) {
                        System.err.println("reverting side angle of first joint went wrong");
                    }
                }
            }
            if (firstBoneEndPosition.y > floorHeight+floorDistanceEps &&
                    random.nextFloat() < firstJointZAngleProbability && firstJoint.movementPossible(nearerToFloor, false)) {
                firstJoint.setNewFirstAngle(nearerToFloor, angleStepSize);
                firstBoneEndPosition = firstBoneSetTransformAndCalculateWorldPosition();
                if (!bonePositionsOverFloor(floorHeight)) {
                    firstJoint.resetCurrentFirstAngle();
                    firstBoneEndPosition = firstBoneSetTransformAndCalculateWorldPosition();
                }
            }
            secondBoneEndPosition = secondBone.getWorldPosition();
            if (secondBoneEndPosition.y > floorHeight+floorDistanceEps &&
                    random.nextFloat() < secondJointAngleProbability && secondJoint.movementPossible(nearerToFloor)) {
                secondJoint.setNewAngle(nearerToFloor, angleStepSize);
                secondBoneEndPosition = secondBoneSetTransformAndCalculateWorldPosition();
                if (!bonePositionsOverFloor(floorHeight)) {
                    secondJoint.resetAngle();
                    secondBoneEndPosition = secondBoneSetTransformAndCalculateWorldPosition();
                }
            }

            if (flooredSecondBone) {
                endPosition = secondBoneEndPosition;
            } else {
                thirdBoneEndPosition = thirdBone.getWorldPosition();
                if (thirdBoneEndPosition.y > floorHeight+floorDistanceEps &&
                        random.nextFloat() < thirdJointAngleProbability && thirdJoint.movementPossible(nearerToFloor)) {
                    thirdJoint.setNewAngle(nearerToFloor, angleStepSize);
                    thirdBoneEndPosition = thirdBoneSetTransformAndCalculateWorldPosition();
                    if (!bonePositionsOverFloor(floorHeight)) {
                        thirdJoint.resetAngle();
                        thirdBoneEndPosition = thirdBoneSetTransformAndCalculateWorldPosition();
                    }
                }
                endPosition = thirdBoneEndPosition;
            }

            if (!bonePositionsOverFloor(floorHeight)) {
                System.err.println("Something went terribly wrong with floored leg positioning!");
            }

            step++;
            if (Math.toDegrees(angleStepSize) > 0.1) {
                float newDistance = endPosition.y - floorHeight;
                if (Math.abs(newDistance-oldDistance) < 0.1) {
                    angleStepSize *= 1f/2f;
                } else {
                    angleStepSize *= 4f/5f;
                }
            }
        }

        if (Math.abs(endPosition.y-floorHeight) < floorDistanceEps) {
            if (flooredSecondBone) { // adjust foot
                Vector3f localDir = new Vector3f(0f, 1f, 0f);
                secondBone.calculateWorldTransform().applyOnVector(localDir);
                float angle = new Vector3f(1f, 0f, 0f).angle(new Vector3f(localDir.x, localDir.y, 0f));
                thirdJoint.setCurrentAngle(-angle);
                thirdBone.setTransform(thirdJoint.calculateChildTransform(thirdBone.getBoundingBox()));
            }
            return true;
        } else {
            System.err.println("Could not reach floor");
            return false;
        }
    }

    public void setInitialAngleStepSize(float initialAngleStepSize) {
        this.initialAngleStepSize = initialAngleStepSize;
    }

    public void setFirstJointXAngleProbability(float firstJointXAngleProbability) {
        this.firstJointXAngleProbability = firstJointXAngleProbability;
    }

    public void setFirstJointZAngleProbability(float firstJointZAngleProbability) {
        this.firstJointZAngleProbability = firstJointZAngleProbability;
    }

    public void setSecondJointAngleProbability(float secondJointAngleProbability) {
        this.secondJointAngleProbability = secondJointAngleProbability;
    }

    public void setThirdJointAngleProbability(float thirdJointAngleProbability) {
        this.thirdJointAngleProbability = thirdJointAngleProbability;
    }

    private Point3f firstBoneSetTransformAndCalculateWorldPosition() {
        firstBone.setTransform(firstJoint.calculateChildTransform(firstBone.getBoundingBox()));
        return firstBone.getWorldPosition();
    }

    private Point3f secondBoneSetTransformAndCalculateWorldPosition() {
        secondBone.setTransform(secondJoint.calculateChildTransform(secondBone.getBoundingBox()));
        return secondBone.getWorldPosition();
    }

    private Point3f thirdBoneSetTransformAndCalculateWorldPosition() {
        thirdBone.setTransform(thirdJoint.calculateChildTransform(thirdBone.getBoundingBox()));
        return thirdBone.getWorldPosition();
    }

    private boolean bonePositionsOverFloor(float floorHeight) {
        boolean valid = firstBone.getWorldPosition().y > floorHeight-floorDistanceEps;
        valid = valid && secondBone.getWorldPosition().y > floorHeight-floorDistanceEps;
        valid = valid && thirdBone.getWorldPosition().y > floorHeight-floorDistanceEps;
        return valid;
    }
}
