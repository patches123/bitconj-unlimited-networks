package org.bitcoin.networks.impl;

import static org.junit.Assert.*;

import org.bitcoinj.params.AbstractBitcoinNetParams;
import org.junit.Before;
import org.junit.Test;

public class WorkingNetworkParametersTest {

	@Before
	public void setUp() throws Exception {
	}

	// To test this comment out every method that calls WorkingNetworkParameters.get().
	// Otherwise those methods would have created an instance of WorkingNetworkParameters
	// which will then get used by this one.
	@Test
	public void testCreateNonWorkingNetworkParameters() throws Exception {
		// this tests the default parameters which are non-working
		WorkingNetworkParameters.pathToNetworkProperties = "nonexisting";
		AbstractBitcoinNetParams network = WorkingNetworkParameters.get();
		
		assertNotEquals(network, null);
	}
	
	@Test
	public void testCreateWorkingNetworkParametersUsingPropertiesFile() throws Exception {
		// this tests the default parameters from network.properties file
		AbstractBitcoinNetParams network = WorkingNetworkParameters.get();
		
		assertNotEquals(network, null);
	}
}
