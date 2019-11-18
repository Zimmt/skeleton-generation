package skeleton;

import util.BoundingBox;

import javax.media.j3d.Transform3D;
import java.util.List;

public interface SkeletonPart {

    String getID();
    SkeletonPart getParent();
    boolean hasParent();
    List<SkeletonPart> getChildren();
    boolean hasChildren();
    boolean isTerminal();
}
