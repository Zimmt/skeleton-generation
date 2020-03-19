package skeleton.replacementRules;

import skeleton.SpinePart;
import skeleton.elements.SkeletonPart;
import skeleton.elements.nonterminal.FrontPart;
import skeleton.elements.nonterminal.ShoulderGirdle;
import skeleton.elements.terminal.Head;
import skeleton.elements.terminal.ShoulderVertebra;
import skeleton.elements.terminal.TerminalElement;
import skeleton.elements.terminal.Vertebra;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Generates
 * - non terminal shoulder girdle
 * - terminal vertebrae between root and shoulder girdle
 * - terminal vertebra that is parent of shoulder girdle
 * - terminal vertebrae of neck
 * - terminal head
 */
public class FrontPartRule extends ReplacementRule {

    private final String inputID = "front part";

    public String getInputID() {
        return inputID;
    }

    public List<SkeletonPart> apply(SkeletonPart skeletonPart) {
        // if rule is not compatible return element unchanged
        if (!isApplicableTo(skeletonPart)) {
            return Arrays.asList(skeletonPart);
        }

        FrontPart frontPart = (FrontPart) skeletonPart;
        List<SkeletonPart> generatedParts = new ArrayList<>();

        Tuple2f frontBackInterval = new Point2f(frontPart.getParent().getFrontPartJoint().getSpinePosition(), 0f);
        Vector3f vertebraScale = new Vector3f(10f, 10f, 10f);
        Vector3f chestVertebraScale = new Vector3f(10f, 100f, 120f);
        List<Vertebra> frontBack = frontPart.getGenerator().generateVertebraeInInterval(frontPart, SpinePart.BACK,
                frontBackInterval, 10, vertebraScale, Optional.of(chestVertebraScale),
                frontPart.getParent(), frontPart.getParent().getFrontPartJoint());
        frontPart.getParent().removeChild(frontPart);

        ShoulderGirdle shoulderGirdle = generateShoulderGirdle(frontPart, frontBack, frontBack.size()-1);
        generatedParts.add(shoulderGirdle);
        generatedParts.addAll(frontBack); // shoulder vertebra is changed, so it has to be set after generating the shoulder girdle

        Tuple2f neckInterval = new Point2f(1f, 0f);
        List<Vertebra> neck = frontPart.getGenerator().generateVertebraeInInterval(frontPart, SpinePart.NECK,
                neckInterval, 10, vertebraScale, Optional.empty(), shoulderGirdle.getParent(), shoulderGirdle.getParent().getJoint());

        ShoulderGirdle secondShoulderGirdle = generateShoulderGirdle(frontPart, neck, neck.size()-3); // todo are there always enough vertebrae on neck?
        generatedParts.add(secondShoulderGirdle);
        generatedParts.addAll(neck); // shoulder vertebra is changed, so it has to be set after generating the shoulder girdle

        Head head = generateHead(frontPart, new Vector3f(50f, 25f, 30f), neck.get(neck.size() - 1));
        generatedParts.add(head);

        return generatedParts;
    }

    private ShoulderGirdle generateShoulderGirdle(FrontPart frontPart, List<Vertebra> vertebrae, int shoulderVertebraPosition) {
        Vertebra toBeShoulderVertebra = vertebrae.get(shoulderVertebraPosition);
        ShoulderVertebra shoulderVertebra = new ShoulderVertebra(toBeShoulderVertebra);
        toBeShoulderVertebra.getParent().replaceChild(toBeShoulderVertebra, shoulderVertebra);
        vertebrae.set(shoulderVertebraPosition, shoulderVertebra);

        ShoulderGirdle shoulderGirdle = new ShoulderGirdle(shoulderVertebra, frontPart);
        shoulderVertebra.addChild(shoulderGirdle);
        return shoulderGirdle;
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
