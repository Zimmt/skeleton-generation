package skeleton;

import javax.media.j3d.Transform3D;
import java.util.ArrayList;
import java.util.List;

public class InitialElement implements NonTerminalElement {

    private Transform3D relativePosition;
    private String id;
    private List<SkeletonPart> children;

    public InitialElement() {
        this.relativePosition = new Transform3D(); // identity
        this.id = "initialElement";
        this.children = new ArrayList<>();
    }

    public Transform3D getTransform() {
        return relativePosition;
    }

    public String getID() {
        return id;
    }

    public SkeletonPart getParent() {
        return null;
    }

    public boolean hasParent() {
        return false;
    }

    public List<SkeletonPart> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }
}
