package skeleton;

import skeleton.elements.joints.Joint;
import skeleton.elements.joints.SpineOrientedJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import skeleton.elements.terminal.Rib;
import skeleton.elements.terminal.TailVertebrae;
import skeleton.elements.terminal.TerminalElement;
import skeleton.elements.terminal.Vertebra;
import util.BoundingBox;
import util.CubicBezierCurve;
import util.TransformationMatrix;

import javax.vecmath.Point2d;
import javax.vecmath.Point2f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector3f;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpineData implements Serializable {

    public static float vertebraYZScale = 10f;
    public static float maxRibYScale = 100f;

    // there are about 15 thoraic vertebrae and 10 vertebrae on the lower back
    // but as root is in the middle of the back curve I distributed the vertebrae more evenly
    public static int frontBackVertebraCount = 13;
    public static int backBackVertebraCount = 12;

    private Integer neckVertebraCount = null;
    private Integer tailVertebraCount = null;

    private CubicBezierCurve neck;
    private CubicBezierCurve back;
    private CubicBezierCurve tail;

    private Tuple2f chestIntervalOnBack;

    private transient Random random = new Random();

    public SpineData(List<Point2d> spinePoints) {
        if (spinePoints.size() != 10) {
            System.err.println("Cannot create spine!");
        }
        this.neck = new CubicBezierCurve(new Point2f(spinePoints.get(0)), new Point2f(spinePoints.get(1)), new Point2f(spinePoints.get(2)), new Point2f(spinePoints.get(3)));
        this.back = new CubicBezierCurve(new Point2f(spinePoints.get(3)), new Point2f(spinePoints.get(4)), new Point2f(spinePoints.get(5)), new Point2f(spinePoints.get(6)));
        this.tail = new CubicBezierCurve(new Point2f(spinePoints.get(6)), new Point2f(spinePoints.get(7)), new Point2f(spinePoints.get(8)), new Point2f(spinePoints.get(9)));
        this.chestIntervalOnBack = new Point2f(0f, (new Random().nextFloat())*2/3f);
        System.out.println("Chest interval: " + chestIntervalOnBack);
    }

    public CubicBezierCurve getPart(SpinePart spinePart) {
        switch (spinePart) {
            case NECK:
                return getNeck();
            case TAIL:
                return getTail();
            default: // BACK
                return getBack();
        }
    }

    public CubicBezierCurve[] getAll() {
        return new CubicBezierCurve[] {neck, back, tail};
    }

    public CubicBezierCurve getNeck() {
        return neck;
    }

    public CubicBezierCurve getBack() {
        return back;
    }

    public CubicBezierCurve getTail() {
        return tail;
    }

    public boolean isInChestInterval(float backSpinePosition) {
        return backSpinePosition <= chestIntervalOnBack.y;
    }

    /**
     * ! This function assumes, that the chest interval starts at 0 !
     * chest function: -[(1.5 * 1/intervalWidth * x - 0.8)^4 - 1] (only in chest interval)
     * chest function(backSpinePosition) * maxRibYScale = rib y length
     */
    public float getRibLength(float backSpinePosition) {
        if (!isInChestInterval(backSpinePosition)) {
            return 0;
        } else {
            return (float) -(Math.pow(1.8f / chestIntervalOnBack.y * backSpinePosition - 0.9f, 4) - 1f) * maxRibYScale;
        }
    }

    public int getNeckVertebraCount(boolean hasWings) {
        if (neckVertebraCount == null) {
            if (!hasWings) {
                neckVertebraCount = 7;
            } else {
                neckVertebraCount = 10 + random.nextInt(21);
            }
        }
        return neckVertebraCount;
    }

    public int getTailVertebraCount() {
        if (tailVertebraCount == null) {
            tailVertebraCount = 5 + random.nextInt(16);
        }
        return tailVertebraCount;
    }

    /**
     * The vertebra are generated from the left side of the interval to the right.
     * If the left float is greater than the right one, then the vertebra are generated in negative direction on the curve.
     * Child vertebrae are added to their parents.
     * For scale SpineData values for y and z are used, x is the maximum space available.
     * @param spinePart if spinePart is tail then the last three vertebrae are replaced by one instance of TailVertebrae
     * @param interval has to contain two floats between 0 and 1
     * @param vertebraCount number of vertebra that shall be generated (equally spaced)
     * @param firstParent element that shall be parent of the first vertebra generated
     * @return the generated vertebra
     */
    public List<TerminalElement> generateVertebraeAndRibsInInterval(NonTerminalElement ancestor, SpinePart spinePart, Tuple2f interval, int vertebraCount,
                                                                    TerminalElement firstParent, Joint firstParentJoint) {

        ArrayList<TerminalElement> generatedParts = new ArrayList<>();
        if (interval.x < 0 || interval.y < 0 || interval.x > 1 || interval.y > 1) {
            System.err.println(String.format("Invalid interval [%f, %f]", interval.x, interval.y));
            return generatedParts;
        }

        BoundingBox boundingBox = new BoundingBox(new Vector3f(1f, SpineData.vertebraYZScale, SpineData.vertebraYZScale)); // x scale is replaced anyway

        float totalIntervalLength = Math.abs(interval.y - interval.x);
        float sign = interval.y > interval.x ? 1f : -1f;
        float oneIntervalStep = sign * totalIntervalLength / (float) vertebraCount;;

        Vertebra parent = null;
        for (int i = 0; i < vertebraCount; i++) {
            boolean tailVertebrae = spinePart == SpinePart.TAIL && i == vertebraCount-3;

            float spinePosition;
            float childSpineEndPosition;
            if (parent == null) {
                spinePosition = interval.x;
                childSpineEndPosition = spinePosition + oneIntervalStep;
                if (firstParentJoint instanceof SpineOrientedJoint) {
                    ((SpineOrientedJoint) firstParentJoint).setChildSpineEndPosition(childSpineEndPosition, spinePart);
                }
            } else {
                spinePosition = parent.getSpineJoint().getSpinePosition();
                if (i == vertebraCount-1 || tailVertebrae) {
                    childSpineEndPosition = interval.y;
                } else {
                    childSpineEndPosition = spinePosition + oneIntervalStep;
                }
                parent.getSpineJoint().setChildSpineEndPosition(childSpineEndPosition, spinePart);
            }

            BoundingBox childBox = boundingBox.cloneBox();
            Point2f startPosition = ancestor.getGenerator().getSkeletonMetaData().getSpine().getPart(spinePart).apply(spinePosition);
            Point2f endPosition = ancestor.getGenerator().getSkeletonMetaData().getSpine().getPart(spinePart).apply(childSpineEndPosition);
            childBox.setXLength((float) Math.sqrt(
                    Math.pow(startPosition.x - endPosition.x, 2) +
                            Math.pow(startPosition.y - endPosition.y, 2)));

            Joint joint = parent == null ? firstParentJoint : parent.getSpineJoint();
            TransformationMatrix transform = joint.calculateChildTransform(childBox);
            transform.translate(Vertebra.getLocalTranslationFromJoint(childBox));

            Vertebra child;
            TerminalElement parentElement = parent == null ? firstParent : parent;
            if (tailVertebrae) {
                child = new TailVertebrae(transform, childBox, parentElement, ancestor, sign > 0, spinePart, childSpineEndPosition);
                i += 2;
            } else {
                child = new Vertebra(transform, childBox, parentElement, ancestor, sign > 0, spinePart, childSpineEndPosition);
            }
            parentElement.addChild(child);
            generatedParts.add(child);

            boolean rib = spinePart == SpinePart.BACK && isInChestInterval(spinePosition);
            if (rib) {
                float ribLengthEvaluationPos = sign > 0 ? spinePosition : childSpineEndPosition;
                generatedParts.add(generateRibForVertebra(ancestor, child, getRibLength(ribLengthEvaluationPos)));
            }

            parent = child;
        }

        return generatedParts;
    }

    private Rib generateRibForVertebra(NonTerminalElement ancestor, Vertebra vertebra, float yScale) {
        float xzScale = vertebra.getBoundingBox().getXLength();
        BoundingBox boundingBox = new BoundingBox(new Vector3f(
                xzScale, yScale, xzScale
        ));
        TransformationMatrix transform = vertebra.getRibJoint().calculateChildTransform(boundingBox);
        transform.translate(Rib.getLocalTranslationFromJoint(boundingBox));

        Rib rib = new Rib(transform, boundingBox, vertebra, ancestor);
        vertebra.addChild(rib);

        return rib;
    }
}
