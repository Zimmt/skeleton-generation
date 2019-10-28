package skeleton;

import java.util.List;

public interface NonTerminalElement extends SkeletonPart {
    List<ReplacementRule> getRules();
}
