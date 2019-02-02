package org.heuros.pair.sp;

import org.heuros.pair.heuro.DutyParam;

public class NodeQualityMetric {

	private NodeQualityVector parent = null;
	private QualityMetric qual = null;
	private NodeQualityMetric prevNodeMetric = null;
	private NodeQualityMetric nextNodeMetric = null;

	public NodeQualityMetric(NodeQualityVector parent,
								QualityMetric nodeQm) {
		this.parent = parent;
		this.qual = new QualityMetric(nodeQm);
	}

	public NodeQualityMetric(NodeQualityVector parent,
								DutyParam dp) {
		this.parent = parent;
		this.qual = new QualityMetric(parent.getNodeOwner(), dp);
	}

	public void reset() {
		this.qual.reset();
		this.parent = null;
		this.nextNodeMetric = null;
		this.prevNodeMetric = null;
	}

	public NodeQualityVector getParent() {
		return parent;
	}

	public NodeQualityMetric getPrevNodeMetric() {
		return prevNodeMetric;
	}

	public void setPrevNodeMetric(NodeQualityMetric prevNodeMetric) {
		this.prevNodeMetric = prevNodeMetric;
	}

	public NodeQualityMetric getNextNodeMetric() {
		return nextNodeMetric;
	}

	public void setNextNodeMetric(NodeQualityMetric nextNodeMetric) {
		this.nextNodeMetric = nextNodeMetric;
	}

	public QualityMetric getQual() {
		return qual;
	}

	public String toString() {
		NodeQualityMetric hNqm = this;
		StringBuilder sb = new StringBuilder();
		while (true) {
			sb.append(hNqm.getQual())
				.append("\n")
				.append(hNqm.parent.getNodeOwnerQm())
				.append("\n")
				.append(hNqm.parent.getNodeOwner())
				.append("\n");
			hNqm = hNqm.getNextNodeMetric();
			if (hNqm == null)
				break;
		}
		return sb.toString();
	}
}
