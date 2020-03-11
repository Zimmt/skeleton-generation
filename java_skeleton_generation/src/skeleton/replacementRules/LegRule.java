package skeleton.replacementRules;

import skeleton.ExtremityData;
import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.Leg;
import skeleton.elements.terminal.Foot;
import skeleton.elements.terminal.Pelvic;
import skeleton.elements.terminal.Shin;
import skeleton.elements.terminal.Thigh;
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
 * - terminal thigh
 * - terminal shin
 * - terminal foot
 */
public class LegRule extends ReplacementRule {

    private final String inputID = "leg";

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart skeletonPart) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(skeletonPart)) {
            return Arrays.asList(skeletonPart);
        }
        System.out.print("Leg generation... ");

        Leg leg = (Leg) skeletonPart;
        List<SkeletonPart> generatedParts = new ArrayList<>();
        ExtremityData extremityData = leg.getGenerator().getSkeletonMetaData().getExtremities();

        Vector3f thighScale = new Vector3f(
                0.4f * leg.getParent().getBoundingBox().getXLength(),
                extremityData.getLengthUpperLeg(),
                0.2f * leg.getParent().getBoundingBox().getZLength());
        Thigh thigh = generateThigh(thighScale, leg);
        generatedParts.add(thigh);

        Vector3f shinScale = new Vector3f(
                0.8f * thigh.getBoundingBox().getXLength(),
                extremityData.getLengthLowerLeg(),
                0.8f * thigh.getBoundingBox().getZLength());
        Shin shin = generateShin(shinScale, leg, thigh);
        generatedParts.add(shin);

        Vector3f footScale = new Vector3f(
                0.8f * shin.getBoundingBox().getXLength(),
                extremityData.getLengthFoot(),
                2f * shin.getBoundingBox().getZLength());
        Foot foot = generateFoot(footScale, leg, shin);
        generatedParts.add(foot);

        if (leg.getGenerator().getSkeletonMetaData().getExtremities().getFlooredLegs() > 0) {
            findFlooredPosition(leg.getParent(), thigh, shin, foot, leg.getGenerator().getSkeletonMetaData().getExtremities().getFlooredAnkleWristProbability());
        } else {
            findFloatingPosition(leg.getParent(), thigh, shin, foot);
        }
        System.out.println("...finished.");

        return generatedParts;
    }

    private Thigh generateThigh(Vector3f scale, Leg leg) {
        BoundingBox boundingBox = new BoundingBox(scale);
        TransformationMatrix transform = leg.getParent().getLegJoint().calculateChildTransform(boundingBox);

        Thigh thigh = new Thigh(transform, boundingBox, leg.getParent(), leg, false);
        leg.getParent().replaceChild(leg, thigh);
        return thigh;
    }

    private Shin generateShin(Vector3f scale, Leg leg, Thigh thigh) {
        BoundingBox boundingBox = new BoundingBox(scale);
        TransformationMatrix transform = thigh.getJoint().calculateChildTransform(boundingBox);

        Shin shin = new Shin(transform, boundingBox, thigh, leg, false);
        thigh.addChild(shin);
        return shin;
    }

    private Foot generateFoot(Vector3f scale, Leg leg, Shin shin) {
        BoundingBox boundingBox = new BoundingBox(scale);
        TransformationMatrix transform = shin.getJoint().calculateChildTransform(boundingBox);

        Foot foot = new Foot(transform, boundingBox, shin, leg);
        shin.addChild(foot);
        return foot;
    }

    private void findFloatingPosition(Pelvic pelvic, Thigh thigh, Shin shin, Foot foot) {
        pelvic.getLegJoint().setCurrentFirstAngle((float) Math.toRadians(90));
        pelvic.getLegJoint().setCurrentSecondAngle((float) Math.toRadians(90));
        thigh.setTransform(pelvic.getLegJoint().calculateChildTransform(thigh.getBoundingBox()));

        thigh.getJoint().setCurrentAngle(0f);
        shin.setTransform(thigh.getJoint().calculateChildTransform(shin.getBoundingBox()));

        shin.getJoint().setCurrentAngle(0f);
        foot.setTransform(shin.getJoint().calculateChildTransform(foot.getBoundingBox()));
    }

    /**
     * Adapts angles of leg until a position is reached where the floor is touched
     * @param flooredAnkleProbability probability for the ankle to touch the floor (otherwise the tip of the foot will be used)
     * @return if it was successful; reasons for failing could be that
     * - the leg is too short to reach the floor
     * - or the maximum number of steps was exceeded (but angles have already been changed)
     */
    private boolean findFlooredPosition(Pelvic pelvic, Thigh thigh, Shin shin, Foot foot, float flooredAnkleProbability) {
        pelvic.getLegJoint().setChild(thigh);
        thigh.getJoint().setChild(shin);
        shin.getJoint().setChild(foot);

        float eps = 1f;
        float angleStepSize = (float) Math.toRadians(20);
        int maxSteps = 40;
        int step = 0;

        float pelvicSideAngleP = 0.5f;
        float pelvicFrontAngleP = 0.3f;
        float thighShinAngleP = 0.7f;
        float footSideAngleP = 0.9f;
        float oppositeDirP = 0f;
        Random random = new Random();
        boolean flooredAnkle = random.nextFloat() < flooredAnkleProbability;
        System.out.print("floored ankle: " + flooredAnkle + "... ");
        pelvic.getGenerator().getSkeletonMetaData().getExtremities().setFlooredAnkleWristProbability(flooredAnkle); // other extremities do the same
        float floorHeight = pelvic.getGenerator().getSkeletonMetaData().getExtremities().getFloorHeight();

        Point3f endPosition;
        if (flooredAnkle) {
            endPosition = shin.getWorldPosition();
        } else {
            endPosition = foot.getWorldPosition();
        }

        while (Math.abs(endPosition.y - floorHeight) > eps && step < maxSteps) {
            //System.out.println("Distance to floor is " + (endPosition.y-floorHeight) + ", angle step size: " + Math.toDegrees(angleStepSize));
            boolean nearerToFloor = endPosition.y-floorHeight > 0;

            if (random.nextFloat() < pelvicSideAngleP && pelvic.getLegJoint().movementPossible(nearerToFloor, true)) {
                pelvic.getLegJoint().setNewSecondAngle((random.nextFloat() < oppositeDirP) != nearerToFloor, angleStepSize);
            }
            if (random.nextFloat() < pelvicFrontAngleP && pelvic.getLegJoint().movementPossible(nearerToFloor, false)) {
                //System.out.println("pelvic front angle");
                pelvic.getLegJoint().setNewFirstAngle((random.nextFloat() < oppositeDirP) != nearerToFloor, angleStepSize);
            }
            thigh.setTransform(pelvic.getLegJoint().calculateChildTransform(thigh.getBoundingBox()));

            if (random.nextFloat() < thighShinAngleP && thigh.getJoint().movementPossible(nearerToFloor)) {
                thigh.getJoint().setNewAngle((random.nextFloat() < oppositeDirP) != nearerToFloor, angleStepSize*2/3f);
            }
            shin.setTransform(thigh.getJoint().calculateChildTransform(shin.getBoundingBox()));

            if (flooredAnkle) {
                endPosition = shin.getWorldPosition();
            } else {
                if (random.nextFloat() < footSideAngleP && shin.getJoint().movementPossible(nearerToFloor)) {
                    shin.getJoint().setNewAngle((random.nextFloat() < oppositeDirP) != nearerToFloor, angleStepSize);
                }
                foot.setTransform(shin.getJoint().calculateChildTransform(foot.getBoundingBox()));

                endPosition = foot.getWorldPosition();
            }

            step++;
            if (Math.toDegrees(angleStepSize) > 1) {
                angleStepSize *= 9f/10f;
            }
        }

        //System.out.print("finding floored position needed " + step + " steps ");
        //System.out.println("Final distance to floor: " + (endPosition.y-floorHeight));

        if (Math.abs(endPosition.y-floorHeight) < eps) {
            if (flooredAnkle) { // adjust foot todo also change angle to world y axis ?
                Vector3f localDir = new Vector3f(0f, 1f, 0f);
                shin.calculateWorldTransform().applyOnVector(localDir);
                float angle = new Vector3f(1f, 0f, 0f).angle(new Vector3f(localDir.x, localDir.y, 0f));
                shin.getJoint().setCurrentAngle(-angle);
                foot.setTransform(shin.getJoint().calculateChildTransform(foot.getBoundingBox()));
            }
            return true;
        } else {
            System.err.println("Could not reach floor");
            return false;
        }
    }
}
