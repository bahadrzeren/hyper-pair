package org.heuros.pair.sp;

import org.heuros.data.model.DutyView;

public class NodeQualityMetric {

	private DutyView nodeOwner = null;
	private NodeQualityMetric nextNodeMetric = null;
	private QualityMetric qual = null;

	public NodeQualityMetric(DutyView nodeOwner,
								QualityMetric nodeQm,
								NodeQualityMetric nextNodeMetric) {
		this.qual = new QualityMetric(nodeQm);
		this.nodeOwner = nodeOwner;
		this.nextNodeMetric = nextNodeMetric;
	}

	public NodeQualityMetric(DutyView nodeOwner,
 									int numOfCoveringsInDuty,
									int blockTimeOfCoveringsInDuty,
									int priority,
									NodeQualityMetric nextNodeMetric) {
		this.qual = new QualityMetric(nodeOwner, numOfCoveringsInDuty, blockTimeOfCoveringsInDuty, priority);
		this.nodeOwner = nodeOwner;
		this.nextNodeMetric = nextNodeMetric;
	}

	public void reset() {
		this.qual.reset();
		this.nodeOwner = null;
		this.nextNodeMetric = null;
	}

	public DutyView getNodeOwner() {
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
