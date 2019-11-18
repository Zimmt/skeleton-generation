package skeleton.replacementRules;

import skeleton.SkeletonPart;

import java.util.List;

public interface ReplacementRule {

    String getInputID();
    List<SkeletonPart> apply(SkeletonPart part);
}
