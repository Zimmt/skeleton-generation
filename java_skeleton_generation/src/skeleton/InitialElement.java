package skeleton;

import java.util.List;

public class InitialElement implements NonTerminalElement {

    private List<ReplacementRule> rules;

    public InitialElement() {
        ReplacementRule rule = new TestRule(this);
    }

    @Override
    public List<ReplacementRule> getRules() {
        return rules;
    }
}
