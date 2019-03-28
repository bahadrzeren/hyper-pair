package org.heuros.pair.enumerator;

import org.heuros.data.model.Duty;

public interface PairListener {
	public void onPairingFound(Duty[] pairing, int fromNdxInc, int toNdxExc, int numOfDhs, int totalActiveBlockTime);
}
