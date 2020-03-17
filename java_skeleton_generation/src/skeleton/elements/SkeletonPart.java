package skeleton.elements;

import skeleton.SkeletonGenerator;
import skeleton.elements.nonterminal.NonTerminalElement;
import skeleton.elements.terminal.TerminalElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SkeletonPart {

    private int id;

    private TerminalElement parent; // parent in hierarchy of body parts; that can only be parts that are in current skeleton (no ancestors)
    private List<SkeletonPart> children; // in hierarchy of body parts
    private NonTerminalElement ancestor; // element of which this part was created by a replacement rule

    private SkeletonGenerator generator;

    /**
     * Use this constructor only if this skeleton part has no _ancestor_ (and therefore no parent)
     * so only for whole body element
     */
    protected SkeletonPart(SkeletonGenerator generator) {
        this.id = generator.getNextBoneId();
        this.parent = null;
        this.children = new ArrayList<>();
        this.ancestor = null;
        this.generator = generator;
    }

    /**
     * Use this constructor only if this skeleton part _has_ an ancestor.
     * The ancestor is used to set the skeleton generator attribute.
     */
    protected SkeletonPart(TerminalElement parent, NonTerminalElement ancestor) {
        this.parent = parent;
        this.children = new ArrayList<>();
        this.ancestor = ancestor;
        this.generator = ancestor.getGenerator();
        this.id = this.generator.getNextBoneId();
    }

    public abstract String getKind();
    public abstract boolean isTerminal();
    public abstract boolean isMirrored();

    public int getId() {
        return id;
    }

    public boolean addChild(SkeletonPart child) {
        return children.add(child);
    }

    public boolean addChildren(SkeletonPart ... parts) {
        return children.addAll(Arrays.asList(parts));
    }

    public boolean addChildren(List<SkeletonPart> parts) {
        return children.addAll(parts);
    }

    public boolean removeChild(SkeletonPart child) {
        return children.remove(child);
    }

    public boolean replaceChild(SkeletonPart oldChild, SkeletonPart newChild) {
        boolean successful = removeChild(oldChild);
        successful = addChild(newChild) && successful;
        return  successful;
    }

    public List<SkeletonPart> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public TerminalElement getParent() {
        return parent;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public NonTerminalElement getAncestor() {
        return ancestor;
    }

    public boolean hasAncestor() {
        return ancestor != null;
    }

    public SkeletonGenerator getGenerator() {
        return this.generator;
    }
}
