package skeleton.replacementRules;

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

        for (PelvisJoint pelvisJoint : leg.getParent().getLegJoints()) {
            ExtremityPositioning extremityPositioning = pelvisJoint.getExtremityPositioning();

            Thigh thigh = generateThigh(leg, pelvisJoint, extremityPositioning.getExtremityKind());
            generatedParts.add(thigh);

            Shin shin = generateShin(leg, thigh, extremityPositioning.getExtremityKind());
            generatedParts.add(shin);

            Foot foot = generateFoot(leg, shin);
            generatedParts.add(foot);

            extremityPositioning.setBonesAndJoints(pelvisJoint, thigh.getJoint(), shin.getJoint(), thigh, shin, foot);
            extremityPositioning.findPosition();
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
