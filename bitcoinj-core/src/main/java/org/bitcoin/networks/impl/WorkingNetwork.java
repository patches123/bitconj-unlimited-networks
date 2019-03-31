package org.bitcoin.networks.impl;

import org.bitcoin.networks.api.INetwork;
import org.bitcoinj.core.NetworkParameters;

public class WorkingNetwork implements INetwork {

	private NetworkParameters networkParameters;

	public WorkingNetwork(final NetworkParameters networkParameters) {
		this.networkParameters = networkParameters;
	}
	
	@Override
	public NetworkParameters getNetworkParameters() {
		return this.networkParameters;
	}
	
	

}
