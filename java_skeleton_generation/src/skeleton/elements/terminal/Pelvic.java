package skeleton.elements.terminal;

import skeleton.SpinePart;
import skeleton.elements.ExtremityKind;
import skeleton.elements.joints.SpineOrientedJoint;
import skeleton.elements.joints.leg.PelvicJoint;
import skeleton.elements.nonterminal.NonTerminalElement;
import util.BoundingBox;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Hüfte
 */
public class Pelvic extends TerminalElement {

    private final String kind = "pelvic";
    private SpineOrientedJoint tailJoint;
    private PelvicJoint firstJoint;
    private PelvicJoint secondJoint;

    public Pelvic(TransformationMatrix transform, BoundingBox boundingBox, TerminalElement parent, NonTerminalElement ancestor,
                  float tailJointSpinePosition, ExtremityKind[] extremityKinds) {
        super(transform, boundingBox, parent, ancestor);
        this.tailJoint = new SpineOrientedJoint(this, Pelvic.getTailJointPosition(boundingBox), SpinePart.TAIL, tailJointSpinePosition, parent.getGenerator());
        if (extremityKinds.length == 1) {
            this.firstJoint = PelvicJoint.newSpecificPelvicJoint(this, Pelvic.getOnlyLegJointPosition(boundingBox, extremityKinds[0]), extremityKinds[0]);
        } else if (extremityKinds.length == 2) {
            this.firstJoint = PelvicJoint.newSpecificPelvicJoint(this, Pelvic.getFirstLegJointPosition(boundingBox, extremityKinds[0]), extremityKinds[0]);
            this.secondJoint = PelvicJoint.newSpecificPelvicJoint(this, Pelvic.getSecondLegJointPosition(boundingBox, extremityKinds[1]), extremityKinds[1]);
        }
    }

    public String getKind() {
        return kind;
    }

    public SpineOrientedJoint getTailJoint() {
        return tailJoint;
    }

    public List<PelvicJoint> getLegJoints() {
        List<PelvicJoint> jointList = new ArrayList<>(2);
        if (firstJoint != null) jointList.add(firstJoint);
        if (secondJoint != null) jointList.add(secondJoint);
        return jointList;
    }

    public boolean isMirrored() { return false; }

    public Pelvic calculateMirroredElement(TerminalElement parent, Optional<TerminalElement> mirroredParent) {
        System.out.println("Tried to mirror an element that should not be mirrored!");
        return null;
    }

    /**
     * @return the translation to move the joint between this element and its parent from this origin somewhere else.
     */
    public static Vector3f getLocalTranslationFromJoint(BoundingBox boundingBox) {
        return new Vector3f(0f, -boundingBox.getYLength()/2f, -boundingBox.getZLength()/2f);
    }

    /**
     * @return the relative position for the joint between this element and the tail
     */
    private static Point3f getTailJointPosition(BoundingBox boundingBox) {
        return new Point3f(boundingBox.getXLength(), boundingBox.getYLength()/2f, boundingBox.getZLength()/2f);
    }

    /**
     * @return the relative positions for the joints between this element and it's first child
     */
    private static Point3f getFirstLegJointPosition(BoundingBox childBoundingBox, ExtremityKind extremityKind) {
        return getLegJointPosition(childBoundingBox, extremityKind,1f/4f);
    }

    /**
     * @return the relative position for the joint between this element and it's second child
     */
    private static Point3f getSecondLegJointPosition(BoundingBox childBoundingBox, ExtremityKind extremityKind) {
        return getLegJointPosition(childBoundingBox, extremityKind,3f/4f);
    }

    /**
     * Only use this if the shoulder has only one child!
     * @return the relative position for the joint between this element and it's child
     */
    private static Point3f getOnlyLegJointPosition(BoundingBox childBoundingBox, ExtremityKind extremityKind) {
        return getLegJointPosition(childBoundingBox, extremityKind, 1f/2f);
    }

    private static Point3f getLegJointPosition(BoundingBox childBoundingBox, ExtremityKind extremityKind, float relativeXPosition) {
        float yValue = 0f;
        if (extremityKind == ExtremityKind.FIN) {
            yValue = childBoundingBox.getYLength();
        }
        return new Point3f(childBoundingBox.getXLength()*relativeXPosition, yValue, childBoundingBox.getZLength()/4f);
    }
}
