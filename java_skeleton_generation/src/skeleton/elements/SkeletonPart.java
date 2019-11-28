package skeleton.elements;

import util.BoundingBox;
import util.TransformationMatrix;

import javax.media.j3d.Transform3D;
import javax.vecmath.Point3f;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SkeletonPart {

    private TransformationMatrix transform; // position and rotation in relation to the coordinate system of parent
    // joint constraints for the joint between this part and parent can optionally be added here
    // (these constraints may not be needed at all levels of abstraction)
    private BoundingBox boundingBox;

    private SkeletonPart parent; // parent in hierarchy of body parts; that can only be parts that are in current skeleton (no ancestors)
    private List<SkeletonPart> children; // in hierarchy of body parts
    private SkeletonPart ancestor; // element of which this part was created by a replacement rule

    protected SkeletonPart(TransformationMatrix transform, BoundingBox boundingBox, SkeletonPart parent, SkeletonPart ancestor) {
        this.boundingBox = boundingBox;
        this.transform  = transform;
        this.parent = parent;
        this.children = new ArrayList<>();
        this.ancestor = ancestor;
    }

    public abstract String getID();
    public abstract boolean isTerminal();
    public abstract boolean isMirrored();

    public TransformationMatrix getTransform() { return transform; }

    public Point3f getWorldPosition() {
        TransformationMatrix t = new TransformationMatrix(transform);
        SkeletonPart parent = this;
        while (parent.hasParent()) {
            parent = parent.getParent();
            t = TransformationMatrix.multiply(t, parent.getTransform());
        }
        Point3f position = new Point3f(); // origin
        t.apply(position);

        return position;
    }

    public BoundingBox getBoundingBox() { return boundingBox; }

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

    public void setParent(SkeletonPart parent) {
        this.parent = parent;
    }

    public SkeletonPart getParent() {
        return parent;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public SkeletonPart getAncestor() {
        return ancestor;
    }

    public boolean hasAncestor() {
        return ancestor != null;
    }
}
