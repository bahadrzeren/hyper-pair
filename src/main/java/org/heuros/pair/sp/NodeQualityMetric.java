package org.heuros.pair.sp;

import org.heuros.data.model.Duty;
import org.heuros.pair.heuro.DutyParam;

public class NodeQualityMetric {

	private Duty nodeOwner = null;
	private NodeQualityMetric nextNodeMetric = null;
	private QualityMetric qual = null;

	public NodeQualityMetric(Duty nodeOwner,
								QualityMetric nodeQm,
								NodeQualityMetric nextNodeMetric) {
		this.qual = new QualityMetric(nodeQm);
		this.nodeOwner = nodeOwner;
		this.nextNodeMetric = nextNodeMetric;
	}

	public NodeQualityMetric(Duty nodeOwner,
								DutyParam dp,
								NodeQualityMetric nextNodeMetric) {
		this.qual = new QualityMetric(nodeOwner, dp);
		this.nodeOwner = nodeOwner;
		this.nextNodeMetric = nextNodeMetric;
	}

	public void reset() {
		this.qual.reset();
		this.nodeOwner = null;
		this.nextNodeMetric = null;
	}

	public Duty getNodeOwner() {
		return nodeOwner;
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
}
