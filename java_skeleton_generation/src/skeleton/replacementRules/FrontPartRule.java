package skeleton.replacementRules;

import skeleton.SpinePart;
import skeleton.elements.SkeletonPart;
import skeleton.elements.joints.DummyJoint;
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
 * - terminal vertebrae between root and shoulder girdle TODO: add chest
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

        Tuple2f frontBackInterval = new Point2f(frontPart.getEndPosition(), 0f);
        Vector3f vertebraScale = new Vector3f(10f, 10f, 10f);
        List<Vertebra> frontBack = frontPart.getGenerator().generateVertebraeInInterval(frontPart, SpinePart.BACK,
                frontBackInterval, 10, vertebraScale, frontPart.getParent(), ((RootVertebra) frontPart.getParent()).getFrontPartJoint()); //todo: remove cast
        frontPart.getParent().removeChild(frontPart);
        generatedParts.addAll(frontBack);

        Vertebra toBeShoulderVertebra = frontBack.get(frontBack.size()-1);
        Point3f shoulderJointPosition = new Point3f(toBeShoulderVertebra.getBoundingBox().getXLength()/2f, 0f, 0f);
        DummyJoint shoulderJoint = new DummyJoint(shoulderJointPosition);
        ShoulderVertebra shoulderVertebra = new ShoulderVertebra(toBeShoulderVertebra, shoulderJoint);
        frontBack.get(frontBack.size() - 2).replaceChild(toBeShoulderVertebra, shoulderVertebra);

        ShoulderGirdle shoulderGirdle = new ShoulderGirdle(shoulderVertebra, frontPart);
        shoulderVertebra.addChild(shoulderGirdle);
        generatedParts.add(shoulderGirdle);

        Tuple2f neckInterval = new Point2f(1f, 0f);
        List<Vertebra> neck = frontPart.getGenerator().generateVertebraeInInterval(frontPart, SpinePart.NECK,
                neckInterval, 10, vertebraScale, shoulderVertebra, shoulderVertebra.getJoint());
        generatedParts.addAll(neck);

        Head head = generateHead(frontPart, new Vector3f(50f, 25f, 30f), neck.get(neck.size() - 1));
        generatedParts.add(head);

        return generatedParts;
    }

    /**
     * position: middle of right side is the end point of the neck part of the spine
     * joint rotation point: middle of the right side
     */
    private Head generateHead(FrontPart frontPart, Vector3f boundingBoxScale, TerminalElement parent) {
        BoundingBox headBox = BoundingBox.defaultBox();
        headBox.scale(boundingBoxScale);

        Point3f neckStartPoint = frontPart.getGenerator().getSkeletonMetaData().getSpine().getNeck().apply3d(0f);
        Point3f globalHeadPosition = new Point3f(neckStartPoint);
        globalHeadPosition.sub(new Point3f(headBox.getXLength(), headBox.getYLength() / 2f, headBox.getZLength() / 2f));

        TransformationMatrix headTransform = TransformationMatrix.getInverse(parent.calculateWorldTransform());
        headTransform.translate(new Vector3f(globalHeadPosition));

        Head head = new Head(headTransform, headBox, parent, frontPart);
        parent.addChild(head);

        return head;
    }
}
