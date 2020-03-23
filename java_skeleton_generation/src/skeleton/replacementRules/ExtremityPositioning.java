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

    // these values are only used to find floored position
    private float floorDistanceEps = 1f;
    private float initialAngleStepSize = (float) Math.toRadians(20);
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

        firstJoint.setChild(firstBone);
        secondJoint.setChild(secondBone);
        thirdJoint.setChild(thirdBone);
    }

    /**
     * sets the following angles:
     * - first joint: first angle 90°, second angle: like world x-axis
     * - second joint: 0°
     * - third joint: 0°
     */
    public void findFloatingPosition() {
        firstJoint.setCurrentFirstAngle(0);

        Vector3f localY = new Vector3f(0f, -1f, 0f);
        firstBone.getParent().calculateWorldTransform().applyOnVector(localY);
        Vector3f worldX = new Vector3f(1f, 0f, 0f);
        float secondAngle = worldX.angle(localY); // turn direction is always positive

        firstJoint.setCurrentSecondAngle(secondAngle);
        firstBone.setTransform(firstJoint.calculateChildTransform(firstBone.getBoundingBox()));

        secondJoint.setCurrentAngle(0f);
        secondBone.setTransform(secondJoint.calculateChildTransform(secondBone.getBoundingBox()));

        thirdJoint.setCurrentAngle(0f);
        thirdBone.setTransform(thirdJoint.calculateChildTransform(thirdBone.getBoundingBox()));
    }

    /**
     * sets the following angles:
     * - first joint: first angle 0°, second angle: like world y-axis
     * - second joint: -90°
     * - third joint: 0°
     */
    public void findArmPosition() {
        firstJoint.setCurrentFirstAngle(0f);

        Vector3f localY = new Vector3f(0f, -1f, 0f);
        firstBone.getParent().calculateWorldTransform().applyOnVector(localY);
        Vector3f worldY = new Vector3f(0f, -1f, 0f);
        float secondAngle = worldY.angle(localY); // turn direction is (most probably) always positive (and if not the angle is small)

        firstJoint.setCurrentSecondAngle(secondAngle);
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
     * ! initial position is determined by the inital angles set by joints
     * @param flooredSecondBone if end of second bone should touch the floor (otherwise the third bone will be used)
     * @return if it was successful; reasons for failing could be that
     * - the bones are too short to reach the floor
     * - or the maximum number of steps was exceeded (but angles have already been changed)
     */
    public boolean findFlooredPosition(boolean flooredSecondBone) {
        int step = 0;
        float angleStepSize = initialAngleStepSize;
        float floorHeight = firstBone.getGenerator().getSkeletonMetaData().getExtremities().getFloorHeight();

        Point3f firstBoneEndPosition = firstBone.getWorldPosition();
        Point3f secondBoneEndPosition = secondBone.getWorldPosition();
        Point3f thirdBoneEndPosition = thirdBone.getWorldPosition();

        if (firstBoneEndPosition.y < floorHeight-floorDistanceEps || secondBoneEndPosition.y < floorHeight-floorDistanceEps || thirdBoneEndPosition.y < floorHeight-floorDistanceEps ) {
            System.err.println("Other start position needed! Bone end position already below floor.");
            return false;
        }

        if (boneOverturned(1) || boneOverturned(3)) {
            System.err.println("Other start position needed! There is an 'overturned' bone.");
            return false;
        }

        Point3f endPosition;
        if (flooredSecondBone) {
            endPosition = secondBoneEndPosition;
        } else {
            endPosition = thirdBoneEndPosition;
        }

        int maxSteps = 50;
        float otherDirectionProbability = 0f;
        while (Math.abs(endPosition.y - floorHeight) > floorDistanceEps && step < maxSteps) {
            boolean nearerToFloor = true;
            float oldDistance = endPosition.y - floorHeight;
            //System.out.println("Distance to floor: " + oldDistance);

            if (firstBoneEndPosition.y > floorHeight+floorDistanceEps &&
                    random.nextFloat() < firstJointXAngleProbability && firstJoint.movementPossible(nearerToFloor, true)) {
                firstJoint.setNewSecondAngle((random.nextFloat() < otherDirectionProbability) != nearerToFloor, angleStepSize);
                firstBoneEndPosition = firstBoneSetTransformAndCalculateWorldPosition();
                if (!bonePositionsOverFloor(floorHeight, flooredSecondBone) || boneOverturned(1)) {
                    firstJoint.resetCurrentSecondAngle();
                    firstBoneEndPosition = firstBoneSetTransformAndCalculateWorldPosition();
                }
            }
            if (firstBoneEndPosition.y > floorHeight+floorDistanceEps &&
                    random.nextFloat() < firstJointZAngleProbability && firstJoint.movementPossible(nearerToFloor, false)) {
                firstJoint.setNewFirstAngle((random.nextFloat() < otherDirectionProbability) != nearerToFloor, angleStepSize);
                firstBoneEndPosition = firstBoneSetTransformAndCalculateWorldPosition();
                if (!bonePositionsOverFloor(floorHeight, flooredSecondBone) || boneOverturned(1)) {
                    firstJoint.resetCurrentFirstAngle();
                    firstBoneEndPosition = firstBoneSetTransformAndCalculateWorldPosition();
                }
            }
            secondBoneEndPosition = secondBone.getWorldPosition();
            if (secondBoneEndPosition.y > floorHeight+floorDistanceEps &&
                    random.nextFloat() < secondJointAngleProbability && secondJoint.movementPossible(nearerToFloor)) {
                secondJoint.setNewAngle((random.nextFloat() < otherDirectionProbability) != nearerToFloor, angleStepSize);
                secondBoneEndPosition = secondBoneSetTransformAndCalculateWorldPosition();
                if (!bonePositionsOverFloor(floorHeight, flooredSecondBone) || boneOverturned(2)) {
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
                    thirdJoint.setNewAngle((random.nextFloat() < otherDirectionProbability *2) != nearerToFloor, angleStepSize);
                    thirdBoneEndPosition = thirdBoneSetTransformAndCalculateWorldPosition();
                    if (!bonePositionsOverFloor(floorHeight, flooredSecondBone) || boneOverturned(3)) {
                        thirdJoint.resetAngle();
                        thirdBoneEndPosition = thirdBoneSetTransformAndCalculateWorldPosition();
                    }
                }
                endPosition = thirdBoneEndPosition;
            }

            if (!bonePositionsOverFloor(floorHeight, flooredSecondBone)) {
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

    private boolean bonePositionsOverFloor(float floorHeight, boolean flooredSecondBone) {
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
        Vector3f lokalYAxis = new Vector3f(0f, 1f, 0f);
        bone.calculateWorldTransform().applyOnVector(lokalYAxis);

        if (boneNumber == 1 && firstBone instanceof UpperArm) {
            return lokalYAxis.x > 0;
        } else {
            return lokalYAxis.x < 0;
        }
    }
}
