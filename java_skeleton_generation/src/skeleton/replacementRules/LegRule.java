package skeleton.replacementRules;

import skeleton.ExtremityData;
import skeleton.elements.SkeletonPart;
import skeleton.elements.joints.ExtremityKind;
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
        ExtremityKind extremityKind = leg.getParent().getLegJoint().getExtremityKind();

        Vector3f thighScale = new Vector3f(
                0.4f * leg.getParent().getBoundingBox().getXLength(),
                extremityData.getLengthUpperLeg(),
                0.2f * leg.getParent().getBoundingBox().getZLength());
        Thigh thigh = generateThigh(thighScale, leg, extremityKind);
        generatedParts.add(thigh);

        Vector3f shinScale = new Vector3f(
                0.8f * thigh.getBoundingBox().getXLength(),
                extremityData.getLengthLowerLeg(),
                0.8f * thigh.getBoundingBox().getZLength());
        Shin shin = generateShin(shinScale, leg, thigh, extremityKind);
        generatedParts.add(shin);

        Vector3f footScale = new Vector3f(
                0.8f * shin.getBoundingBox().getXLength(),
                extremityData.getLengthFoot(),
                2f * shin.getBoundingBox().getZLength());
        Foot foot = generateFoot(footScale, leg, shin);
        generatedParts.add(foot);

        ExtremityPositioning extremityPositioning = new ExtremityPositioning(
                leg.getParent().getLegJoint(), thigh.getJoint(), shin.getJoint(), thigh, shin, foot);

        if (extremityKind == ExtremityKind.FLOORED_LEG) {
            boolean flooredAnkle = (new Random()).nextFloat() < extremityData.getFlooredAnkleWristProbability();
            System.out.print("floored ankle: " + flooredAnkle + "... ");

            // other extremities do the same
            thigh.getGenerator().getSkeletonMetaData().getExtremities().setFlooredAnkleWristProbability(flooredAnkle);

            extremityPositioning.findFlooredPosition(flooredAnkle);
        } else {
            extremityPositioning.findFloatingPosition();
        }
        System.out.println("...finished.");

        return generatedParts;
    }

    private Thigh generateThigh(Vector3f scale, Leg leg, ExtremityKind extremityKind) {
        BoundingBox boundingBox = new BoundingBox(scale);
        TransformationMatrix transform = leg.getParent().getLegJoint().calculateChildTransform(boundingBox);

        Thigh thigh = new Thigh(transform, boundingBox, leg.getParent(), leg, false, extremityKind);
        leg.getParent().replaceChild(leg, thigh);
        return thigh;
    }

    private Shin generateShin(Vector3f scale, Leg leg, Thigh thigh, ExtremityKind extremityKind) {
        BoundingBox boundingBox = new BoundingBox(scale);
        TransformationMatrix transform = thigh.getJoint().calculateChildTransform(boundingBox);

        Shin shin = new Shin(transform, boundingBox, thigh, leg, false, extremityKind);
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
}
