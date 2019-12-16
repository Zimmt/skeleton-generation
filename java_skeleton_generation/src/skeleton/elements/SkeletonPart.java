package skeleton.elements;

import skeleton.SkeletonGenerator;
import skeleton.elements.nonterminal.NonTerminalElement;
import skeleton.elements.terminal.TerminalElement;
import util.TransformationMatrix;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SkeletonPart {

    private int id;

    private TransformationMatrix transform; // position and rotation in relation to the coordinate system of parent
    private Point3f jointRotationPoint; // rotation center of the joint between this part and it's parent in the coordinate system of the parent

    private TerminalElement parent; // parent in hierarchy of body parts; that can only be parts that are in current skeleton (no ancestors)
    private List<SkeletonPart> children; // in hierarchy of body parts
    private NonTerminalElement ancestor; // element of which this part was created by a replacement rule

    private SkeletonGenerator generator;

    /* Use this constructor only if this skeleton part has no ancestor (and therefore no parent)
     */
    protected SkeletonPart(TransformationMatrix transform, SkeletonGenerator generator) {
        this.id = generator.getNextBoneId();
        this.transform = transform;
        this.jointRotationPoint = null;
        this.parent = null;
        this.children = new ArrayList<>();
        this.ancestor = null;
        this.generator = generator;
    }

    /* Use this constructor only if this skeleton part _has_ an ancestor.
     * The ancestor is used to set the skeleton generator attribute.
     */
    protected SkeletonPart(TransformationMatrix transform, Point3f jointRotationPoint,
                           TerminalElement parent, NonTerminalElement ancestor) {
        this.transform  = transform;
        this.jointRotationPoint = jointRotationPoint;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.ancestor = ancestor;
        this.generator = ancestor.getGenerator();
        this.id = this.generator.getNextBoneId();
    }

    public abstract String getKind();
    public abstract boolean isTerminal();
    public abstract boolean isMirrored();

    public int getId() {
        return id;
    }

    public TransformationMatrix getTransform() { return transform; }

    /**
     * @return the transformation matrix that transforms from the local coordinate system of this skeleton part
     * to the world space
     */
    public TransformationMatrix getWorldTransform() {
        TransformationMatrix worldTransform = new TransformationMatrix(transform);
        SkeletonPart parent = this;
        while (parent.hasParent()) {
            parent = parent.getParent();
            worldTransform = TransformationMatrix.multiply(worldTransform, parent.getTransform());
        }
        return worldTransform;
    }

    public Point3f getWorldPosition() {
        TransformationMatrix t = getWorldTransform();
        Point3f position = new Point3f(); // origin
        t.applyOnPoint(position);

        return position;
    }

    public void rotateAroundXAxisOfJoint(float angle) {
        translateJointRotationPointToOriginOfParent();
        transform.rotateAroundX(angle);
        translateOriginOfParentToJointRotationPoint();
    }

    public void rotateAroundYAxisOfJoint(float angle) {
        translateJointRotationPointToOriginOfParent();
        transform.rotateAroundY(angle);
        translateOriginOfParentToJointRotationPoint();
    }

    public void rotateAroundZAxisOfJoint(float angle) {
        translateJointRotationPointToOriginOfParent();
        transform.rotateAroundZ(angle);
        translateOriginOfParentToJointRotationPoint();
    }


    public boolean addChild(SkeletonPart child) {
        return children.add(child);
    }

    public boolean addChildren(SkeletonPart ... parts) {
        return children.addAll(Arrays.asList(parts));
    }

    public boolean addChildren(List<SkeletonPart> parts) {
        return children.addAll(parts);
    }

    public boolean removeChild(SkeletonPart child) {
        return children.remove(child);
    }

    public boolean replaceChild(SkeletonPart oldChild, SkeletonPart newChild) {
        return removeChild(oldChild) && addChild(newChild);
    }

    public List<SkeletonPart> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public TerminalElement getParent() {
        return parent;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public NonTerminalElement getAncestor() {
        return ancestor;
    }

    public boolean hasAncestor() {
        return ancestor != null;
    }

    public SkeletonGenerator getGenerator() {
        return this.generator;
    }


    private void translateJointRotationPointToOriginOfParent() {
        Vector3f translationToOrigin = new Vector3f(jointRotationPoint);
        translationToOrigin.scale(-1f);
        transform.translate(translationToOrigin);
    }

    private void translateOriginOfParentToJointRotationPoint() {
        transform.translate(new Vector3f(jointRotationPoint));
    }
}
