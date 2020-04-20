package skeleton.replacementRules;

import skeleton.ExtremityData;
import skeleton.elements.ExtremityKind;
import skeleton.elements.joints.Joint;
import skeleton.elements.joints.OneAngleBasedJoint;
import skeleton.elements.joints.TwoAngleBasedJoint;
import skeleton.elements.joints.XZAngleBasedJoint;
import skeleton.elements.terminal.TerminalElement;
import skeleton.elements.terminal.UpperArm;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector3f;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExtremityPositioning implements Serializable {

    private final ExtremityKind extremityKind;

    private transient XZAngleBasedJoint firstJoint; // joint between parent of fist bone and first bone
    private transient OneAngleBasedJoint secondJoint; // joint between first and second bone
    private transient OneAngleBasedJoint thirdJoint; // joint between second and third bone
    private transient Joint[] joints;

    private transient TerminalElement firstBone;
    private transient TerminalElement secondBone;
    private transient TerminalElement thirdBone;
    private transient TerminalElement[] bones;

    // these angles are only saved here to reconstruct the skeleton from file
    private Tuple2f firstJointAngles;
    private float secondJointAngle;
    private float thirdJointAngle;

    public ExtremityPositioning(ExtremityKind extremityKind) {
        this.extremityKind = extremityKind;
    }

    public void setBonesAndJoints(XZAngleBasedJoint firstJoint, OneAngleBasedJoint secondJoint, OneAngleBasedJoint thirdJoint,
                                  TerminalElement firstBone, TerminalElement secondBone, TerminalElement thirdBone) {
        this.firstJoint = firstJoint;
        this.secondJoint = secondJoint;
        this.thirdJoint = thirdJoint;
        this.joints = new Joint[] {firstJoint, secondJoint, thirdJoint};
        this.firstBone = firstBone;
        this.secondBone = secondBone;
        this.thirdBone = thirdBone;
        this.bones = new TerminalElement[] {firstBone, secondBone, thirdBone};

        firstJoint.setChild(firstBone);
        secondJoint.setChild(secondBone);
        thirdJoint.setChild(thirdBone);
    }

    public boolean findPosition() {
        if (firstJoint == null || secondJoint == null || thirdJoint == null ||
                firstBone == null || secondBone == null || thirdBone == null) {
            System.err.println("Cannot find position without bones and joints!");
            return false;
        }
        if (firstJointAngles != null) { // reconstruct joint angles from data
            firstJoint.setCurrentFirstAngle(firstJointAngles.x);
            firstJoint.setCurrentSecondAngle(firstJointAngles.y);
            firstBone.setTransform(firstJoint.calculateChildTransform(firstBone.getBoundingBox()));

            secondJoint.setCurrentAngle(secondJointAngle);
            secondBone.setTransform(secondJoint.calculateChildTransform(secondBone.getBoundingBox()));

            thirdJoint.setCurrentAngle(thirdJointAngle);
            thirdBone.setTransform(thirdJoint.calculateChildTransform(thirdBone.getBoundingBox()));
            return true;
        }
        boolean success = true;
        if (extremityKind == ExtremityKind.LEG) {
            ExtremityData extremityData = firstBone.getGenerator().getSkeletonMetaData().getExtremities();
            boolean flooredSecondBone = (new Random()).nextFloat() < extremityData.getFlooredAnkleWristProbability();
            //System.out.print("floored second bone: " + flooredSecondBone + "... ");
            extremityData.setFlooredAnkleWristProbability(flooredSecondBone); // other extremities do the same

            success = findFlooredPosition(flooredSecondBone);
        }
        saveJointAngles();
        return success;
    }

    public ExtremityKind getExtremityKind() {
        return extremityKind;
    }

    /**
     * Adapts angles of joints until a position is reached where the floor is touched
     * ! initial position is determined by the initial angles set by joints
     * @param flooredSecondBone if end of second bone should touch the floor (otherwise the third bone will be used)
     * @return if it was successful; reasons for failing could be that
     * - the bones are too short to reach the floor
     * - or the maximum number of steps was exceeded (but angles have already been changed)
     */
    private boolean findFlooredPosition(boolean flooredSecondBone) {
        float floorHeight = firstBone.getGenerator().getSkeletonMetaData().getExtremities().getFloorHeight();
        float floorDistanceEps = 5f;

        if (anyOverturnedBone()) {
            fixOverturnedBones();
        }

        Point3f firstBoneEndPosition = firstBone.getWorldPosition();
        Point3f secondBoneEndPosition = secondBone.getWorldPosition();
        Point3f thirdBoneEndPosition = thirdBone.getWorldPosition();

        Point3f endPosition;
        if (flooredSecondBone) {
            endPosition = secondBoneEndPosition;
        } else {
            endPosition = thirdBoneEndPosition;
        }

        int step = 0;
        int maxSteps = 50;

        if (!bonePositionsOverFloor(floorHeight, flooredSecondBone, floorDistanceEps) ) {
            // this can happen sometimes (e.g. for Dimetrodon), at least align foot
            System.out.println("Other start position needed! Bone end position already below floor.");
            adjustFoot();
            return false;
        }

        float initialAngleStepSize = (float) Math.toRadians(30);
        float firstJointZAngleProbability = 1f; // first joint x angle is ignored
        float secondJointAngleProbability = 1f;
        float thirdJointAngleProbability = 1f;

        float angleStepSize = initialAngleStepSize;
        while (Math.abs(endPosition.y - floorHeight) > floorDistanceEps && step < maxSteps) {
            float oldDistance = endPosition.y - floorHeight;
            //System.out.println("Distance to floor: " + oldDistance);

            if (Math.abs(secondJoint.getCurrentAngle()) <= Math.toRadians(2) && flooredSecondBone) {
                System.out.println("Leg is too short. Ankle/wrist cannot be floored...");
                flooredSecondBone = false;
                thirdJoint.setCurrentAngle(0f);
                if (!bonePositionsOverFloor(floorHeight, false, floorDistanceEps) || anyOverturnedBone()) {
                    thirdJoint.resetAngle();
                } else {
                    thirdBone.setTransform(thirdJoint.calculateChildTransform(thirdBone.getBoundingBox()));
                }
            }
            if (Math.abs(secondJoint.getCurrentAngle()) <= Math.toRadians(2) && Math.abs(thirdJoint.getCurrentAngle()) <= Math.toRadians(2)) {
                // this happens when shoulder joint is too much above the point where neck and back meet
                // (this is used to determine floor height)
                System.out.println("Leg is too short. It cannot reach the floor.");
                return true;
            }

            if (firstBoneEndPosition.y > floorHeight+floorDistanceEps &&
                    (new Random()).nextFloat() < firstJointZAngleProbability && firstJoint.movementPossible(true, true)) {
                firstBoneEndPosition = tryChangeFirstJointSecondAngle(angleStepSize, floorHeight, flooredSecondBone, floorDistanceEps);
            }
            secondBoneEndPosition = secondBone.getWorldPosition();
            if (secondBoneEndPosition.y > floorHeight+floorDistanceEps &&
                    (new Random()).nextFloat() < secondJointAngleProbability && secondJoint.movementPossible(true)) {
                secondBoneEndPosition = tryChangeOneAngleBasedJointAngle(2, angleStepSize, floorHeight, flooredSecondBone, floorDistanceEps);
            }

            if (flooredSecondBone) {
                endPosition = secondBoneEndPosition;
            } else {
                thirdBoneEndPosition = thirdBone.getWorldPosition();
                if (thirdBoneEndPosition.y > floorHeight+floorDistanceEps &&
                        (new Random()).nextFloat() < thirdJointAngleProbability && thirdJoint.movementPossible(true)) {
                    thirdBoneEndPosition = tryChangeOneAngleBasedJointAngle(3, angleStepSize, floorHeight, flooredSecondBone, floorDistanceEps);
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
                    // distance did not change much, maybe angles are too big and always lead to invalid positions that are reverted
                    // System.out.println("angle step size / 2");
                    angleStepSize *= 1f/2f;
                } else {
                    angleStepSize *= 5f/6f;
                }
            }
        }


        if (flooredSecondBone) { // adjust foot
            adjustFoot();
        }

        float finalDistanceToFloor = Math.abs(endPosition.y-floorHeight);
        boolean successful = finalDistanceToFloor < floorDistanceEps;
        if (!successful) {
            System.err.println("Could not reach floor. Final distance to floor is: " + finalDistanceToFloor);
            System.out.println(String.format("1.front: %f, 1.side: %f, 2.: %f, 3.: %f",
                    Math.toDegrees(firstJoint.getCurrentFirstAngle()), Math.toDegrees(firstJoint.getCurrentSecondAngle()),
                    Math.toDegrees(secondJoint.getCurrentAngle()), Math.toDegrees(thirdJoint.getCurrentAngle())));
        }
        return successful;
    }

    private void adjustFoot() {
        Vector3f localDir = new Vector3f(0f, 1f, 0f);
        secondBone.calculateWorldTransform().applyOnVector(localDir);
        float angle = new Vector3f(1f, 0f, 0f).angle(new Vector3f(localDir.x, localDir.y, 0f));
        thirdJoint.setCurrentAngle(-angle);
        thirdBone.setTransform(thirdJoint.calculateChildTransform(thirdBone.getBoundingBox()));
    }

    private void saveJointAngles() {
        firstJointAngles = new Point2f(firstJoint.getCurrentFirstAngle(), firstJoint.getCurrentSecondAngle());
        secondJointAngle = secondJoint.getCurrentAngle();
        thirdJointAngle = thirdJoint.getCurrentAngle();
    }

    private Point3f tryChangeOneAngleBasedJointAngle(int jointNumber, float angleStepSize, float floorHeight, boolean flooredSecondBone, float floorDistanceEps) {
        OneAngleBasedJoint joint = (OneAngleBasedJoint) getJoint(jointNumber);

        joint.setNewAngle(true, angleStepSize);
        Point3f boneEndPosition = setTransformAndCalculateWorldPositionOfBone(jointNumber);

        if (!bonePositionsOverFloor(floorHeight, flooredSecondBone, floorDistanceEps)) {
            joint.resetAngle();
            boneEndPosition = setTransformAndCalculateWorldPositionOfBone(jointNumber);
        } else if (anyOverturnedBone()) {
            List<Integer> changedBoneIds = fixOverturnedBones();
            if (!bonePositionsOverFloor(floorHeight, flooredSecondBone, floorDistanceEps)) {
                for (Integer id : changedBoneIds) {
                    resetJointAngle(id);
                }
                joint.resetAngle();
            }
            boneEndPosition = setTransformAndCalculateWorldPositionOfBone(jointNumber);
        }
        return boneEndPosition;
    }

    private Point3f tryChangeFirstJointSecondAngle(float angleStepSize, float floorHeight, boolean flooredSecondBone, float floorDistanceEps) {
        firstJoint.setNewSecondAngle(true, angleStepSize);
        Point3f boneEndPosition = setTransformAndCalculateWorldPositionOfBone(1);

        if (!bonePositionsOverFloor(floorHeight, flooredSecondBone, floorDistanceEps)) {
            firstJoint.resetSecondAngle();
            boneEndPosition = setTransformAndCalculateWorldPositionOfBone(1);
        } else if (anyOverturnedBone()) {
            List<Integer> changedBoneIds = fixOverturnedBones();
            if (!bonePositionsOverFloor(floorHeight, flooredSecondBone, floorDistanceEps)) {
                for (Integer id : changedBoneIds) {
                    resetJointAngle(id);
                }
                firstJoint.resetSecondAngle();
            }
            boneEndPosition = setTransformAndCalculateWorldPositionOfBone(1);
        }
        return boneEndPosition;
    }

    private Point3f setTransformAndCalculateWorldPositionOfBone(int boneNumber) {
        TerminalElement bone = getBone(boneNumber);
        bone.setTransform(getJoint(boneNumber).calculateChildTransform(bone.getBoundingBox()));
        return bone.getWorldPosition();
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
        TerminalElement bone = getBone(boneNumber);
        Vector3f lokalYAxis = new Vector3f(0f, -1f, 0f);
        bone.calculateWorldTransform().applyOnVector(lokalYAxis);

        if (boneNumber == 1 && firstBone instanceof UpperArm) {
            return lokalYAxis.x <= 0;
        } else {
            return lokalYAxis.x >= 0;
        }
    }

    private boolean anyOverturnedBone() {
        return boneOverturned(1) || boneOverturned(3);
    }

    /**
     * @return numbers of bones that have been changed
     */
    private List<Integer> fixOverturnedBones() {
        List<Integer> feedback = new ArrayList<>(2);
        float eps = (float) Math.toRadians(5.0);
        Vector3f localYAxis = new Vector3f(0f, -1f, 0f);
        Vector3f posGlobalYAxis = new Vector3f(0f, 1f, 0f);
        Vector3f negGlobalYAxis = new Vector3f(0f, -1f, 0f);

        if (boneOverturned(1)) {
            feedback.add(1);
            firstBone.calculateWorldTransform().applyOnVector(localYAxis);
            float posAngle = localYAxis.angle(posGlobalYAxis) + eps;
            float negAngle = localYAxis.angle(negGlobalYAxis) + eps;
            float angle = posAngle < negAngle ? posAngle : -negAngle;
            if (firstBone instanceof UpperArm) {
                angle = -angle;
            }
            firstJoint.setCurrentSecondAngle(firstJoint.getCurrentSecondAngle() + angle);
            firstBone.setTransform(firstJoint.calculateChildTransform(firstBone.getBoundingBox()));
        }
        if (boneOverturned(3)) {
            feedback.add(3);
            thirdBone.calculateWorldTransform().applyOnVector(localYAxis);
            float posAngle = localYAxis.angle(posGlobalYAxis) + eps;
            float negAngle = localYAxis.angle(negGlobalYAxis) + eps;
            float angle = posAngle < negAngle ? posAngle : -negAngle;
            thirdJoint.setCurrentAngle(thirdJoint.getCurrentAngle() + angle);
            thirdBone.setTransform((thirdJoint.calculateChildTransform(thirdBone.getBoundingBox())));
        }
        if (anyOverturnedBone()) {
            System.err.println("Fixing overturned bone(s) did not work!");
        }
        return feedback;
    }

    private TerminalElement getBone(int number) {
        return bones[number-1];
    }

    private Joint getJoint(int number) {
        return joints[number-1];
    }

    private void resetJointAngle(int jointNumber) {
        Joint joint = getJoint(jointNumber);
        if (joint instanceof TwoAngleBasedJoint) {
            ((TwoAngleBasedJoint) joint).resetSecondAngle();
        } else {
            ((OneAngleBasedJoint) joint).resetAngle();
        }
    }
}
