package skeleton.replacementRules;

import skeleton.elements.SkeletonPart;

import java.util.List;

public abstract class ReplacementRule {

    public abstract String getInputID();
    public abstract List<SkeletonPart> apply(SkeletonPart part);

    public boolean isApplicableTo(SkeletonPart part) {
        return getInputID().equals(part.getID());
    }
}
