package analysis.model;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class CFG {
	private List<CFGNode> nodeList = new ArrayList<>();

	public List<CFGNode> getNodeList() {
		return nodeList;
	}

	public void setNodeList(List<CFGNode> nodeList) {
		this.nodeList = nodeList;
	}

	public void addNode(int index) {
		CFGNode node = new CFGNode(index);
		nodeList.add(node);
	}

	public CFGNode link(int start, int end) {
		CFGNode parent = getNode(start);
		if(parent == null) {
			System.out.println("CFG: Node " + start + " does not exist.");
			System.exit(0);
		}
		CFGNode child = getNode(end);
		if (child == null) {
			System.out.println("CFG: Node " + end + " does not exist.");
			System.exit(0);
		}
		parent.addChild(child);
		return child;
	}

	public CFGNode getNode(int index) {
		for (CFGNode node : nodeList) {
			if (node.index == index) {
				return node;
			}
		}
		return null;
	}

	public void generate(String filename) {
		try {
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			writer.println("digraph " + " {");

			for (CFGNode node : nodeList) {
				if (node.children.size() > 0) {
					for (CFGNode child : node.children) {
						if (child.hasLink()) {
							writer.println(node.index + " -> " + child.index + " [label=\"" + child.linkLabel + "\"];");
						} else {
							writer.println(node.index + " -> " + child.index);
						}
					}
				}
			}
			writer.println("}");
			writer.close();
			System.out.println("Tree generated");
		} catch (FileNotFoundException e) {
			System.out.println("JavaGraph: " + filename + " could not be written to.");
		} catch (UnsupportedEncodingException e) {
			System.out.print("JavaGraph: " + e.getMessage());
		}
	}

	class CFGNode {

		private int index;
		private List<CFGNode> children = new ArrayList<>();
		private String linkLabel;

		public CFGNode(int index) {
			this.index = index;
		}

		public void addChild(CFGNode child) {
			if(child == null) {
				return;
			}
			this.children.add(child);
		}

		public String getLink() {
			return linkLabel;
		}

		public void setLink(String linkLabel) {
			this.linkLabel = linkLabel;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public boolean hasLink() {
			return (linkLabel != null);
		}
	}
}
