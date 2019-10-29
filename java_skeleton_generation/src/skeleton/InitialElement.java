package skeleton;

import java.util.ArrayList;
import java.util.List;

public class InitialElement implements NonTerminalElement {

    private List<ReplacementRule> rules;

    public InitialElement() {
        this.rules = new ArrayList<>();
        ReplacementRule rule = new TestRule(this);
        rules.add(rule);
    }

    @Override
    public List<ReplacementRule> getRules() {
        return rules;
    }
}
