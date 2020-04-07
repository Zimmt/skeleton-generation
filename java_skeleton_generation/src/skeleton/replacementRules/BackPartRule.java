package skeleton.replacementRules;

import skeleton.SkeletonMetaData;
import skeleton.SpineData;
import skeleton.SpinePart;
import skeleton.elements.ExtremityKind;
import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.BackPart;
import skeleton.elements.nonterminal.Leg;
import skeleton.elements.terminal.Pelvis;
import skeleton.elements.terminal.RootVertebra;
import skeleton.elements.terminal.TerminalElement;
import skeleton.elements.terminal.Vertebra;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point2f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Generates
 * - non terminal leg
 * - terminal vertebrae between root and pelvic (+ rib if in chest interval)
 * - terminal pelvis
 * - terminal vertebrae on tail
 */
public class BackPartRule extends ReplacementRule {

    private final String inputID = "back part";
    private static float pelvisZScale = 100f;

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart skeletonPart) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(skeletonPart)) {
            return Arrays.asList(skeletonPart);
        }

        BackPart backPart = (BackPart) skeletonPart;
        RootVertebra rootVertebra = backPart.getParent();
        SkeletonMetaData skeletonMetaData = backPart.getGenerator().getSkeletonMetaData();
        List<SkeletonPart> generatedParts = new ArrayList<>();

        Tuple2f backBackInterval = new Point2f(rootVertebra.getBackPartJoint().getSpinePosition(), 1f);
        List<TerminalElement> backBack = skeletonMetaData.getSpine().generateVertebraeAndRibsInInterval(backPart, SpinePart.BACK,
                backBackInterval, SpineData.backBackVertebraCount, rootVertebra, rootVertebra.getBackPartJoint());
        rootVertebra.removeChild(backPart);
        generatedParts.addAll(backBack);

        Vertebra pelvisParent = null;
        Vertebra tailParent = null;
        int foundVertebraCount = 0;
        for (int i = backBack.size()-1; i >= 0 && foundVertebraCount < 3; i--) {
            if (backBack.get(i) instanceof  Vertebra) {
                foundVertebraCount++;
                if (tailParent == null) {
                    tailParent = (Vertebra) backBack.get(i);
                } else if (foundVertebraCount == 3) {
                    pelvisParent = (Vertebra) backBack.get(i);
                }
            }
        }
        if (tailParent == null || pelvisParent == null) {
            System.err.println("Did not find enough vertebrae on back back!");
            return generatedParts;
        }

        ExtremityKind[] pelvisExtremityKinds = skeletonMetaData.getExtremities().getExtremityKindsForStartingPoint(0);
        if (pelvisExtremityKinds.length > 0) {
            Pelvis pelvis = generatePelvis(backPart, pelvisParent);
            generatedParts.add(pelvis);

            if (!pelvis.getLegJoints().isEmpty()) {
                Leg leg = new Leg(pelvis, backPart);
                pelvis.addChild(leg);
                generatedParts.add(leg);
            } else {
                System.out.println("No legs generated");
            }
        }

        Tuple2f tailInterval = new Point2f(0f, 1f);
        int tailVertebraCount = skeletonMetaData.getSpine().getTailVertebraCount();
        List<TerminalElement> tail = skeletonMetaData.getSpine().generateVertebraeAndRibsInInterval(backPart, SpinePart.TAIL,
                tailInterval, tailVertebraCount, tailParent, tailParent.getSpineJoint());
        generatedParts.addAll(tail);

        return generatedParts;
    }

    private Pelvis generatePelvis(BackPart backPart, Vertebra parent) {
        float xyScale = parent.getBoundingBox().getXLength();
        BoundingBox boundingBox = new BoundingBox(new Vector3f(xyScale, xyScale, pelvisZScale));
        TransformationMatrix transform = parent.getPelvisJoint().calculateChildTransform(boundingBox);
        transform.translate(Pelvis.getLocalTranslationFromJoint(boundingBox));

        Pelvis pelvis = new Pelvis(transform, boundingBox, parent, backPart,
                backPart.getGenerator().getSkeletonMetaData().getExtremities().getExtremityKindsForStartingPoint(0));
        parent.addChild(pelvis);
        return pelvis;
    }
}
