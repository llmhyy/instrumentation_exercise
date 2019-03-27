package analysis;

import java.util.ArrayList;
import java.util.List;

public class CFG {
	private List<CFGNode> nodeList = new ArrayList<>();
	private CFGNode start;
	private List<CFGNode> ends;

	public CFG(List<CFGNode> nodeList, CFGNode start, List<CFGNode> ends) {
		super();
		this.nodeList = nodeList;
		this.start = start;
		this.ends = ends;
	}

	public List<CFGNode> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<CFGNode> nodeList) {
		this.nodeList = nodeList;
	}

	public CFGNode getStart() {
		return start;
	}

	public void setStart(CFGNode start) {
		this.start = start;
	}

	public List<CFGNode> getEnds() {
		return ends;
	}

	public void setEnds(List<CFGNode> ends) {
		this.ends = ends;
	}

}
