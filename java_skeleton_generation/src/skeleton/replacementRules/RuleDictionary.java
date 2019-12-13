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
        List<ReplacementRule> rules = Arrays.asList(rule);

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
        List<ReplacementRule> rules = new ArrayList<>();
        rules.add(new WholeBodyRule());
        /*rules.add(new WholeBodyRule());
        rules.add(new FrontPartRule());
        rules.add(new ShoulderGirdleRule());
        rules.add(new ArmRule());
        rules.add(new NeckRule());
        rules.add(new TorsoRule());
        rules.add(new ChestRule());
        rules.add(new VertebraWithRibRule());
        rules.add(new BackPartRule());
        rules.add(new PelvicGirdleRule());
        rules.add(new LegRule());
        rules.add(new TailRule());*/

        for (ReplacementRule rule : rules) {
            // for each input id at most one rule
            map.put(rule.getInputID(), Arrays.asList(rule));
        }
    }
}
