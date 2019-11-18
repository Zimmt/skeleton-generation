package skeleton;

import util.BoundingBox;

import javax.media.j3d.Transform3D;
import java.util.ArrayList;
import java.util.List;

public class SimpleBone implements TerminalElement {
    private Transform3D relativePosition;
    private BoundingBox boundingBox;
    private String id;
    private SkeletonPart parent;
    private List<SkeletonPart> children;

    public SimpleBone(Transform3D relativePosition, BoundingBox boundingBox, String id, SkeletonPart parent) {
        this.relativePosition = relativePosition;
        this.boundingBox = boundingBox;
        this.id = id;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public Transform3D getTransform() {
        return relativePosition;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public String getID() {
        return id;
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

    public void addChild(SkeletonPart child) {
        children.add(child);
    }
}
