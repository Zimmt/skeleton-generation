package skeleton.elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SkeletonPart {

    private SkeletonPart parent; // parent in hierarchy of body parts; that can only be parts that are in current skeleton (no ancestors)
    private List<SkeletonPart> children; // in hierarchy of body parts
    private SkeletonPart ancestor; // element of which this part was created by a replacement rule

    public SkeletonPart(SkeletonPart parent, SkeletonPart ancestor) {
        this.parent = parent;
        this.children = new ArrayList<>();
        this.ancestor = ancestor;
    }

    public abstract String getID();
    public abstract boolean isTerminal();
    public abstract boolean isMirrored();

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

    public void setParent(SkeletonPart parent) {
        this.parent = parent;
    }

    public SkeletonPart getParent() {
        return parent;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public List<SkeletonPart> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public SkeletonPart getAncestor() {
        return ancestor;
    }

    public boolean hasAncestor() {
        return ancestor != null;
    }
}
