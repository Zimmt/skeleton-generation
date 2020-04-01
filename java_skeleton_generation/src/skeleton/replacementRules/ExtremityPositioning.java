package skeleton.replacementRules;

import skeleton.elements.joints.OneAngleBasedJoint;
import skeleton.elements.joints.XZAngleBasedJoint;
import skeleton.elements.terminal.TerminalElement;
import skeleton.elements.terminal.UpperArm;

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

    private Random random = new Random();

    public ExtremityPositioning(XZAngleBasedJoint firstJoint, OneAngleBasedJoint secondJoint, OneAngleBasedJoint thirdJoint,
                                TerminalElement firstBone, TerminalElement secondBone, TerminalElement thirdBone) {
        this.firstJoint = firstJoint;
        this.secondJoint = secondJoint;
        this.thirdJoint = thirdJoint;
        this.firstBone = firstBone;
        this.secondBone = secondBone;
        this.thirdBone = thirdBone;

        firstJoint.setChild(firstBone);
        secondJoint.setChild(secondBone);
        thirdJoint.setChild(thirdBone);
    }

    /**
     * Adapts angles of joints until a position is reached where the floor is touched
     * ! initial position is determined by the inital angles set by joints
     * @param flooredSecondBone if end of second bone should touch the floor (otherwise the third bone will be used)
     * @return if it was successful; reasons for failing could be that
     * - the bones are too short to reach the floor
     * - or the maximum number of steps was exceeded (but angles have already been changed)
     */
    public boolean findFlooredPosition(boolean flooredSecondBone) {
        float floorHeight = firstBone.getGenerator().getSkeletonMetaData().getExtremities().getFloorHeight();
        float floorDistanceEps = 1f;

        if (anyOverturnedBone()) {
            fixOverturnedBones();
        }

        Point3f firstBoneEndPosition = firstBone.getWorldPosition();
        Point3f secondBoneEndPosition = secondBone.getWorldPosition();
        Point3f thirdBoneEndPosition = thirdBone.getWorldPosition();

        if (firstBoneEndPosition.y < floorHeight-floorDistanceEps || secondBoneEndPosition.y < floorHeight-floorDistanceEps || thirdBoneEndPosition.y < floorHeight-floorDistanceEps ) {
            System.err.println("Other start position needed! Bone end position already below floor.");
            return false;
        }

        Point3f endPosition;
        if (flooredSecondBone) {
            endPosition = secondBoneEndPosition;
        } else {
            endPosition = thirdBoneEndPosition;
        }

        int step = 0;
        int maxSteps = 50;
        float initialAngleStepSize = (float) Math.toRadians(30);
        float firstJointXAngleProbability = 0.5f;
        float firstJointZAngleProbability = 1f;
        float secondJointAngleProbability = 1f;
        float thirdJointAngleProbability = 1f;
        float otherDirectionProbability = 0f;

        float angleStepSize = initialAngleStepSize;
        while (Math.abs(endPosition.y - floorHeight) > floorDistanceEps && step < maxSteps) {
            boolean nearerToFloor = true;
            float oldDistance = endPosition.y - floorHeight;
            System.out.println("Distance to floor: " + oldDistance);
            /*System.out.println(String.format("1.front: %f, 1.side: %f, 2.: %f, 3.: %f",
                    Math.toDegrees(firstJoint.getCurrentFirstAngle()), Math.toDegrees(firstJoint.getCurrentSecondAngle()),
                    Math.toDegrees(secondJoint.getCurrentAngle()), Math.toDegrees(thirdJoint.getCurrentAngle())));*/

            if (firstBoneEndPosition.y > floorHeight+floorDistanceEps &&
                    random.nextFloat() < firstJointZAngleProbability && firstJoint.movementPossible(nearerToFloor, true)) {
                firstJoint.setNewSecondAngle((random.nextFloat() < otherDirectionProbability) != nearerToFloor, angleStepSize);
                firstBoneEndPosition = firstBoneSetTransformAndCalculateWorldPosition();
                if (!bonePositionsOverFloor(floorHeight, flooredSecondBone, floorDistanceEps)) {
                    firstJoint.resetCurrentSecondAngle();
                    firstBoneEndPosition = firstBoneSetTransformAndCalculateWorldPosition();
                } else if (anyOverturnedBone()) {
                    fixOverturnedBones();
                }
            }
            if (firstBoneEndPosition.y > floorHeight+floorDistanceEps &&
                    random.nextFloat() < firstJointXAngleProbability && firstJoint.movementPossible(nearerToFloor, false)) {
                firstJoint.setNewFirstAngle((random.nextFloat() < otherDirectionProbability) != nearerToFloor, angleStepSize);
                firstBoneEndPosition = firstBoneSetTransformAndCalculateWorldPosition();
                if (!bonePositionsOverFloor(floorHeight, flooredSecondBone, floorDistanceEps)) {
                    firstJoint.resetCurrentFirstAngle();
                    firstBoneEndPosition = firstBoneSetTransformAndCalculateWorldPosition();
                } else if (anyOverturnedBone()) {
                    fixOverturnedBones();
                }
            }
            secondBoneEndPosition = secondBone.getWorldPosition();
            if (secondBoneEndPosition.y > floorHeight+floorDistanceEps &&
                    random.nextFloat() < secondJointAngleProbability && secondJoint.movementPossible(nearerToFloor)) {
                secondJoint.setNewAngle((random.nextFloat() < otherDirectionProbability) != nearerToFloor, angleStepSize);
                secondBoneEndPosition = secondBoneSetTransformAndCalculateWorldPosition();
                if (!bonePositionsOverFloor(floorHeight, flooredSecondBone, floorDistanceEps)) {
                    secondJoint.resetAngle();
                    secondBoneEndPosition = secondBoneSetTransformAndCalculateWorldPosition();
                } else if (boneOverturned(3)) {
                    fixOverturnedBones();
                }
            }

            if (flooredSecondBone) {
                endPosition = secondBoneEndPosition;
            } else {
                thirdBoneEndPosition = thirdBone.getWorldPosition();
                if (thirdBoneEndPosition.y > floorHeight+floorDistanceEps &&
                        random.nextFloat() < thirdJointAngleProbability && thirdJoint.movementPossible(nearerToFloor)) {
                    thirdJoint.setNewAngle((random.nextFloat() < otherDirectionProbability) != nearerToFloor, angleStepSize);
                    thirdBoneEndPosition = thirdBoneSetTransformAndCalculateWorldPosition();
                    if (!bonePositionsOverFloor(floorHeight, flooredSecondBone, floorDistanceEps)) {
                        thirdJoint.resetAngle();
                        thirdBoneEndPosition = thirdBoneSetTransformAndCalculateWorldPosition();
                    } else if (boneOverturned(3)) {
                        fixOverturnedBones();
                    }
                }
                endPosition = thirdBoneEndPosition;
            }

            if (!bonePositionsOverFloor(floorHeight, flooredSecondBone, floorDistanceEps)) {
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

        float finalDistanceToFloor = Math.abs(endPosition.y-floorHeight);
        if (finalDistanceToFloor < floorDistanceEps) {
            if (flooredSecondBone) { // adjust foot
                Vector3f localDir = new Vector3f(0f, 1f, 0f);
                secondBone.calculateWorldTransform().applyOnVector(localDir);
                float angle = new Vector3f(1f, 0f, 0f).angle(new Vector3f(localDir.x, localDir.y, 0f));
                thirdJoint.setCurrentAngle(-angle);
                thirdBone.setTransform(thirdJoint.calculateChildTransform(thirdBone.getBoundingBox()));
            }
            return true;
        } else {
            System.err.println("Could not reach floor. Final distance to floor is: " + finalDistanceToFloor);
            return false;
        }
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

    private boolean bonePositionsOverFloor(float floorHeight, boolean flooredSecondBone, float floorDistanceEps) {
        boolean valid = firstBone.getWorldPosition().y > floorHeight-floorDistanceEps;
        valid = valid && secondBone.getWorldPosition().y > floorHeight-floorDistanceEps;
        if (!flooredSecondBone) {
            valid = valid && thirdBone.getWorldPosition().y > floorHeight-floorDistanceEps;
        }
        return valid;
    }

    /**
     * Only for first and third bone
     */
    private boolean boneOverturned(int boneNumber) {
        TerminalElement bone = boneNumber == 1 ? firstBone : thirdBone;
        Vector3f lokalYAxis = new Vector3f(0f, -1f, 0f);
        bone.calculateWorldTransform().applyOnVector(lokalYAxis);

        if (boneNumber == 1 && firstBone instanceof UpperArm) {
            return lokalYAxis.x < 0;
        } else {
            return lokalYAxis.x > 0;
        }
    }

    private boolean anyOverturnedBone() {
        return boneOverturned(1) || boneOverturned(3);
    }

    private void fixOverturnedBones() {
        float eps = (float) Math.toRadians(2.0);
        Vector3f localYAxis = new Vector3f(0f, -1f, 0f);
        Vector3f globalYAxis = new Vector3f(0f, 1f, 0f);

        if (boneOverturned(1)) {
            firstBone.calculateWorldTransform().applyOnVector(localYAxis);
            float angle = localYAxis.angle(globalYAxis) + eps;
            if (firstBone instanceof UpperArm) {
                angle = -angle;
            }
            firstJoint.setCurrentSecondAngle(firstJoint.getCurrentSecondAngle() + angle);
            firstBone.setTransform(firstJoint.calculateChildTransform(firstBone.getBoundingBox()));
        }
        if (boneOverturned(3)) {
            thirdBone.calculateWorldTransform().applyOnVector(localYAxis);
            float angle = localYAxis.angle(globalYAxis) + eps;
            thirdJoint.setCurrentAngle(thirdJoint.getCurrentAngle() + angle);
            thirdBone.setTransform((thirdJoint.calculateChildTransform(thirdBone.getBoundingBox())));
        }
    }
}
