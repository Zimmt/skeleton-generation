package skeleton.replacementRules;

import java.util.*;

public class RuleDictionary {

    private Map<String, List<ReplacementRule>> map;

    public RuleDictionary() {
        this.map = new HashMap<>();

        initializeRules();
    }

    public void addRule(ReplacementRule rule) {
        String key = rule.getInputID();
        List<ReplacementRule> rules = Collections.singletonList(rule);

        if (map.containsKey(key)) {
            rules = map.get(key);
            rules.add(rule);
        }

        map.put(key, rules);
    }

    public List<ReplacementRule> getRules(String id) {
        return map.get(id);
    }

    private void initializeRules() {
        WholeBodyRule wholeBodyRule = new WholeBodyRule();
        map.put(wholeBodyRule.getInputID(), Collections.singletonList(wholeBodyRule));
    }
}
