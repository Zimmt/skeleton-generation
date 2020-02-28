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

        Leg leg = (Leg) skeletonPart;
        List<SkeletonPart> generatedParts = new ArrayList<>();
        ExtremityData extremityData = leg.getGenerator().getSkeletonMetaData().getExtremities();

        Vector3f thighScale = new Vector3f(
                0.6f * leg.getParent().getBoundingBox().getXLength(),
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

        if (!findFlooredPosition(leg.getParent(), thigh, shin, foot, true)) {
            findFlooredPosition(leg.getParent(), thigh, shin, foot, false);
        }

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

    private boolean findFlooredPosition(Pelvic pelvic, Thigh thigh, Shin shin, Foot foot, boolean flooredAnkle) {
        pelvic.getLegJoint().setChild(thigh);
        thigh.getJoint().setChild(shin);
        shin.getJoint().setChild(foot);

        float eps = 1f;
        float angleStepSize = (float) Math.toRadians(10);
        int maxSteps = 10;
        int step = 0;
        Point3f endPosition;
        if (flooredAnkle) {
            endPosition = shin.getWorldPosition();
        } else {
            endPosition = foot.getWorldPosition();
        }
        if (endPosition.y > 0) {
            System.err.println("Leg is too short!");
            return false;
        }

        while (Math.abs(endPosition.y) > eps && step < maxSteps) {
            System.out.println("Distance to floor is " + endPosition.y);
            boolean nearerToFloor = endPosition.y > 0;

            if (pelvic.getLegJoint().movementPossible(nearerToFloor, true)) {
                pelvic.getLegJoint().setNewSideAngle(nearerToFloor, angleStepSize);
            }
            if (pelvic.getLegJoint().movementPossible(nearerToFloor, false)) {
                pelvic.getLegJoint().setNewFrontAngle(nearerToFloor, angleStepSize);
            }
            thigh.setTransform(pelvic.getLegJoint().calculateChildTransform(thigh.getBoundingBox()));

            if (thigh.getJoint().movementPossible(nearerToFloor)) {
                thigh.getJoint().setNewAngle(nearerToFloor, angleStepSize);
            }
            shin.setTransform(thigh.getJoint().calculateChildTransform(shin.getBoundingBox()));

            if (flooredAnkle) {
                endPosition = shin.getWorldPosition();
            } else {
                if (shin.getJoint().movementPossible(nearerToFloor, true)) {
                    shin.getJoint().setNewSideAngle(nearerToFloor, angleStepSize);
                }
                if (shin.getJoint().movementPossible(nearerToFloor, false)) {
                    shin.getJoint().setNewFrontAngle(nearerToFloor, angleStepSize);
                }
                foot.setTransform(shin.getJoint().calculateChildTransform(foot.getBoundingBox()));

                endPosition = foot.getWorldPosition();
            }

            step++;
            angleStepSize /= 2f;
        }

        if (Math.abs(endPosition.y) < eps) {
            if (flooredAnkle) { // adjust foot
                Vector3f localXDir = new Vector3f(1f, 0f, 0f);
                foot.calculateWorldTransform().applyOnVector(localXDir);
                float angle = localXDir.angle(new Vector3f(1f, 0f, 0f)); // todo correct angle?
                shin.getJoint().setCurrentSideAngle(-angle);
                foot.setTransform(shin.getJoint().calculateChildTransform(foot.getBoundingBox()));
            }
            return true;
        } else {
            System.err.println("Could not reach floor");
            return false;
        }
    }
}
