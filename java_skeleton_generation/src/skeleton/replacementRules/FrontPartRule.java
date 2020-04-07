package skeleton.replacementRules;

import skeleton.SkeletonMetaData;
import skeleton.SpineData;
import skeleton.SpinePart;
import skeleton.elements.ExtremityKind;
import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.FrontPart;
import skeleton.elements.nonterminal.ShoulderGirdle;
import skeleton.elements.terminal.*;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generates
 * - non terminal shoulder girdle
 * - terminal vertebrae between root and shoulder girdle (+ rib if in chest interval),
 *      includes vertebra/rib that is parent of shoulder girdle
 * - terminal vertebrae of neck
 * - terminal head
 */
public class FrontPartRule extends ReplacementRule {

    private final String inputID = "front part";
    private static Vector3f headScale = new Vector3f(100f, 40f, 60f);

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart skeletonPart) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(skeletonPart)) {
            return Arrays.asList(skeletonPart);
        }

        FrontPart frontPart = (FrontPart) skeletonPart;
        SkeletonMetaData skeletonMetaData = frontPart.getGenerator().getSkeletonMetaData();
        List<SkeletonPart> generatedParts = new ArrayList<>();

        Tuple2f frontBackInterval = new Point2f(frontPart.getParent().getFrontPartJoint().getSpinePosition(), 0f);
        List<TerminalElement> frontBack = skeletonMetaData.getSpine().generateVertebraeAndRibsInInterval(frontPart, SpinePart.BACK,
                frontBackInterval, SpineData.frontBackVertebraCount, frontPart.getParent(), frontPart.getParent().getFrontPartJoint());
        frontPart.getParent().removeChild(frontPart);

        List<ShoulderGirdle> shoulderGirdles = generateShoulderGirdlesOnOnePosition(frontPart, 1, frontBack, frontBack.size()-1, false);
        generatedParts.addAll(shoulderGirdles);
        generatedParts.addAll(frontBack); // shoulder vertebra is changed, so it has to be set after generating the shoulder girdle

        Tuple2f neckInterval = new Point2f(1f, 0f);
        int neckVertebraCount = skeletonMetaData.getSpine().getNeckVertebraCount(skeletonMetaData.getExtremities().hasWings());
        Vertebra neckParent = getNeckParent(frontBack);
        List<TerminalElement> neck = skeletonMetaData.getSpine().generateVertebraeAndRibsInInterval(frontPart, SpinePart.NECK,
                neckInterval, neckVertebraCount, neckParent, neckParent.getSpineJoint());

        if (skeletonMetaData.getExtremities().hasShoulderOnNeck()) {
            List<ShoulderGirdle> secondShoulderGirdles = generateShoulderGirdlesOnOnePosition(frontPart, 2, neck, neck.size()-3, true);
            generatedParts.addAll(secondShoulderGirdles);
        }
        generatedParts.addAll(neck); // shoulder vertebra might be changed, so it has to be set after generating the shoulder girdle

        Head head = generateHead(frontPart, headScale, neck.get(neck.size() - 1));
        generatedParts.add(head);

        return generatedParts;
    }

    private List<ShoulderGirdle> generateShoulderGirdlesOnOnePosition(FrontPart frontPart, int position, List<TerminalElement> vertebrae, int firstVertebraIndex, boolean onNeck) {
        List<ShoulderGirdle> shoulderGirdles = new ArrayList<>(2);
        ExtremityKind[] extremityKinds = frontPart.getGenerator().getSkeletonMetaData().getExtremities().getExtremityKindsForStartingPoint(position);
        if (extremityKinds.length > 0) {
            shoulderGirdles.add(generateShoulderGirdle(frontPart, vertebrae, firstVertebraIndex, extremityKinds[0], onNeck));
        }
        if (extremityKinds.length > 1) { // find third vertebra or rib of third vertebra
            int foundVertebrae = 0;
            int i = firstVertebraIndex-1;
            for (; i >= 0 && foundVertebrae < 3; i--) {
                if (vertebrae.get(i) instanceof Vertebra) {
                    foundVertebrae++;
                }
            }
            if (vertebrae.get(i+1) instanceof Rib) {
                i++;
            }
            ShoulderGirdle shoulderGirdle = generateShoulderGirdle(frontPart, vertebrae, i, extremityKinds[1], onNeck);
            shoulderGirdles.add(shoulderGirdle);
        }
        return shoulderGirdles;
    }

    /**
     * @param vertebrae contain vertebrae but also can contain ribs
     */
    private ShoulderGirdle generateShoulderGirdle(FrontPart frontPart, List<TerminalElement> vertebrae, int shoulderVertebraIndex, ExtremityKind extremityKind, boolean onNeck) {
        TerminalElement parent = vertebrae.get(shoulderVertebraIndex);

        if (parent instanceof Vertebra) { // vertebra has no rib, vertebra needs to be shoulder vertebra
            Vertebra toBeShoulderVertebra = (Vertebra) vertebrae.get(shoulderVertebraIndex);
            ShoulderVertebra shoulderVertebra = new ShoulderVertebra(toBeShoulderVertebra);
            toBeShoulderVertebra.getParent().replaceChild(toBeShoulderVertebra, shoulderVertebra);
            vertebrae.set(shoulderVertebraIndex, shoulderVertebra);

            parent = shoulderVertebra;
        }

        ShoulderGirdle shoulderGirdle = new ShoulderGirdle(parent, frontPart, extremityKind, onNeck);
        parent.addChild(shoulderGirdle);
        return shoulderGirdle;
    }

    private Vertebra getNeckParent(List<TerminalElement> frontBackVertebrae) {
        if (frontBackVertebrae.get(frontBackVertebrae.size()-1) instanceof Vertebra) {
            return (Vertebra) frontBackVertebrae.get(frontBackVertebrae.size()-1);
        } else {
            return (Vertebra) frontBackVertebrae.get(frontBackVertebrae.size()-2);
        }
    }

    private Head generateHead(FrontPart frontPart, Vector3f boundingBoxScale, TerminalElement parent) {
        BoundingBox headBox = new BoundingBox(boundingBoxScale);

        Point3f globalHeadPosition = Head.getGlobalHeadPosition(frontPart.getGenerator().getSkeletonMetaData().getSpine(), headBox);
        TransformationMatrix headTransform = TransformationMatrix.getInverse(parent.calculateWorldTransform());
        headTransform.translate(new Vector3f(globalHeadPosition));

        Head head = new Head(headTransform, headBox, parent, frontPart);
        parent.addChild(head);
        return head;
    }
}
