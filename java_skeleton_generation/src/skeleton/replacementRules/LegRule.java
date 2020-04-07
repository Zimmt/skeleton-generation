package skeleton.replacementRules;

import skeleton.ExtremityData;
import skeleton.elements.ExtremityKind;
import skeleton.elements.SkeletonPart;
import skeleton.elements.joints.leg.PelvisJoint;
import skeleton.elements.nonterminal.Leg;
import skeleton.elements.terminal.Foot;
import skeleton.elements.terminal.Shin;
import skeleton.elements.terminal.Thigh;
import util.BoundingBox;
import util.TransformationMatrix;

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

        for (PelvisJoint pelvisJoint : leg.getParent().getLegJoints()) {
            ExtremityKind extremityKind = pelvisJoint.getExtremityKind();

            Thigh thigh = generateThigh(leg, pelvisJoint, extremityKind);
            generatedParts.add(thigh);

            Shin shin = generateShin(leg, thigh, extremityKind);
            generatedParts.add(shin);

            Foot foot = generateFoot(leg, shin);
            generatedParts.add(foot);

            ExtremityPositioning extremityPositioning = new ExtremityPositioning(
                    pelvisJoint, thigh.getJoint(), shin.getJoint(), thigh, shin, foot);

            if (extremityKind == ExtremityKind.LEG) {
                boolean flooredAnkle = (new Random()).nextFloat() < extremityData.getFlooredAnkleWristProbability();
                System.out.print("floored ankle: " + flooredAnkle + "... ");

                // other extremities do the same
                thigh.getGenerator().getSkeletonMetaData().getExtremities().setFlooredAnkleWristProbability(flooredAnkle);

                extremityPositioning.findFlooredPosition(flooredAnkle);
            } // else it is a fin nothing needs to be done as position is determined by joints
        }

        System.out.println("...finished.");

        return generatedParts;
    }

    private Thigh generateThigh(Leg leg, PelvisJoint pelvisJoint, ExtremityKind extremityKind) {
        float xzScale = leg.getParent().getBoundingBox().getXLength();
        BoundingBox boundingBox = new BoundingBox(new Vector3f(
                xzScale,
                leg.getGenerator().getSkeletonMetaData().getExtremities().getLengthUpperLeg(),
                xzScale));
        TransformationMatrix transform = pelvisJoint.calculateChildTransform(boundingBox);

        Thigh thigh = new Thigh(transform, boundingBox, leg.getParent(), leg, extremityKind);
        leg.getParent().replaceChild(leg, thigh);
        return thigh;
    }

    private Shin generateShin(Leg leg, Thigh thigh, ExtremityKind extremityKind) {
        float xzScale = leg.getParent().getBoundingBox().getXLength();
        BoundingBox boundingBox = new BoundingBox(new Vector3f(
                xzScale,
                leg.getGenerator().getSkeletonMetaData().getExtremities().getLengthLowerLeg(),
                xzScale));
        TransformationMatrix transform = thigh.getJoint().calculateChildTransform(boundingBox);

        Shin shin = new Shin(transform, boundingBox, thigh, leg, extremityKind);
        thigh.addChild(shin);
        return shin;
    }

    private Foot generateFoot(Leg leg, Shin shin) {
        float xzScale = shin.getBoundingBox().getXLength();
        BoundingBox boundingBox = new BoundingBox(new Vector3f(
                xzScale,
                leg.getGenerator().getSkeletonMetaData().getExtremities().getLengthFoot(),
                xzScale));
        TransformationMatrix transform = shin.getJoint().calculateChildTransform(boundingBox);

        Foot foot = new Foot(transform, boundingBox, shin, leg);
        shin.addChild(foot);
        return foot;
    }
}
