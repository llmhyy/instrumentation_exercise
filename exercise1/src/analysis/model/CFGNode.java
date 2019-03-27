package analysis.model;

import java.util.List;

import org.apache.bcel.generic.Instruction;

public class CFGNode {
	private Instruction instruction;

	private List<CFGNode> parents;
	private List<CFGNode> children;

	public Instruction getInstruction() {
		return instruction;
	}

	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}

	public List<CFGNode> getParents() {
		return parents;
	}

	public void setParents(List<CFGNode> parents) {
		this.parents = parents;
	}

	public List<CFGNode> getChildren() {
		return children;
	}

	public void setChildren(List<CFGNode> children) {
		this.children = children;
	}

}
