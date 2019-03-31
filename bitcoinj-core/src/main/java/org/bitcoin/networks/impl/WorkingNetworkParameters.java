package org.bitcoin.networks.impl;

import static com.google.common.base.Preconditions.checkState;
import static org.bitcoinj.core.Coin.FIFTY_COINS;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.Utils;
import org.bitcoinj.net.discovery.HttpDiscovery;
import org.bitcoinj.params.AbstractBitcoinNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptOpCodes;

public class WorkingNetworkParameters extends AbstractBitcoinNetParams {

	private String paymentProtocol;
	
	private static AbstractBitcoinNetParams instance;
	
	public static String pathToNetworkProperties = Thread.currentThread().getContextClassLoader()
												.getResource("").getPath() + "network.properties";

	private static Block createGenesis(NetworkParameters n) {
        Block genesisBlock = new Block(n, Block.BLOCK_VERSION_GENESIS);
        Transaction t = new Transaction(n);
        try {
            // A script containing the difficulty bits and the following message:
            //
            //   "The Times 03/Jan/2009 Chancellor on brink of second bailout for banks"
            byte[] bytes = Utils.HEX.decode
                    ("04ffff001d0104455468652054696d65732030332f4a616e2f32303039204368616e63656c6c6f72206f6e206272696e6b206f66207365636f6e64206261696c6f757420666f722062616e6b73");
            t.addInput(new TransactionInput(n, t, bytes));
            ByteArrayOutputStream scriptPubKeyBytes = new ByteArrayOutputStream();
            Script.writeBytes(scriptPubKeyBytes, Utils.HEX.decode
                    ("04678afdb0fe5548271967f1a67130b7105cd6a828e03909a67962e0ea1f61deb649f6bc3f4cef38c4f35504e51ec112de5c384df7ba0b8d578a4c702b6bf11d5f"));
            scriptPubKeyBytes.write(ScriptOpCodes.OP_CHECKSIG);
            t.addOutput(new TransactionOutput(n, t, FIFTY_COINS, scriptPubKeyBytes.toByteArray()));
        } catch (Exception e) {
            // Cannot happen.
            throw new RuntimeException(e);
        }
        genesisBlock.addTransaction(t);
        return genesisBlock;
    }
	
	public static synchronized AbstractBitcoinNetParams get() {
		if (instance == null) {
			String networkId = null; 
			int port = -1; 
			String paymentProtocol = null;
			int interval = -1; 
			int targetTimespan = -1;
			BigInteger maxTarget = null;
			int dumpedPrivateKeyHeader = -1; 
			int addressHeader = -1;
			int p2shHeader = -1; 
			String segwitAddressHrp = null;
			long packetMagic = -1;
			int bip32HeaderP2PKHpub = -1; 
			int bip32HeaderP2PKHpriv = -1; 
			int bip32HeaderP2WPKHpub = -1; 
			int bip32HeaderP2WPKHpriv = -1;
			int majorityEnforceBlockUpgrade = -1; 
			int majorityRejectBlockOutdated = -1; 
			int majorityWindow = -1;
			int subsidyDecreaseBlockCount = -1; 
			int spendableCoinbaseDepth = -1;
			String genesisBlockHashCheck = null;
			Map<Integer, Sha256Hash> checkpoints = null;
			String[] dnsSeeds = null;
			HttpDiscovery.Details[] httpSeeds = null;
			int[] addressSeeds = null;
			
			Properties properties = new Properties();
			try {
				
				properties.load(new FileInputStream(pathToNetworkProperties));
				
				networkId = properties.getProperty("networkId");
				port = Integer.parseInt(properties.getProperty("port"));
				paymentProtocol = properties.getProperty("paymentProtocol");
				interval = Integer.parseInt(properties.getProperty("interval"));
				maxTarget = new BigInteger(properties.getProperty("maxTarget"), 16);
				dumpedPrivateKeyHeader = Integer.parseInt(properties.getProperty("dumpedPrivateKeyHeader"));
				addressHeader = Integer.parseInt(properties.getProperty("addressHeader"));
				p2shHeader = Integer.parseInt(properties.getProperty("p2shHeader"));
				segwitAddressHrp = properties.getProperty("segwitAddressHrp");
				packetMagic = Long.parseLong(properties.getProperty("packetMagic").trim());
				bip32HeaderP2PKHpub = Integer.parseInt(properties.getProperty("bip32HeaderP2PKHpub"));
				bip32HeaderP2PKHpriv = Integer.parseInt(properties.getProperty("bip32HeaderP2PKHpriv"));
				bip32HeaderP2WPKHpub = Integer.parseInt(properties.getProperty("bip32HeaderP2WPKHpub"));
				bip32HeaderP2WPKHpriv = Integer.parseInt(properties.getProperty("bip32HeaderP2WPKHpriv"));
				majorityEnforceBlockUpgrade = Integer.parseInt(properties.getProperty("majorityEnforceBlockUpgrade"));
				majorityRejectBlockOutdated = Integer.parseInt(properties.getProperty("majorityRejectBlockOutdated"));
				majorityWindow = Integer.parseInt(properties.getProperty("majorityWindow"));
				subsidyDecreaseBlockCount = Integer.parseInt(properties.getProperty("subsidyDecreaseBlockCount"));
				spendableCoinbaseDepth = Integer.parseInt(properties.getProperty("spendableCoinbaseDepth"));
				genesisBlockHashCheck = properties.getProperty("genesisBlockHashCheck");
				
				String genesisBlockTime = properties.getProperty("genesisBlockTime");
				String genesisDifficultyTarget = properties.getProperty("genesisDifficultyTarget");
				String genesisBlockNonce = properties.getProperty("genesisBlockNonce");
				if (genesisBlockTime != null && genesisDifficultyTarget != null && genesisBlockNonce != null) {
					createGenesis(new WorkingNetworkParameters());
					getGenesisBlock().setTime(new Long(genesisBlockTime));
					getGenesisBlock().setDifficultyTarget(new Long(genesisDifficultyTarget));
					getGenesisBlock().setNonce(new Long(genesisBlockNonce));
				}
				
				String delimitedSeparatedCheckpoints = properties.getProperty("checkpoints");
				if (delimitedSeparatedCheckpoints != null) {
					String[] arrayCheckpoints = delimitedSeparatedCheckpoints.split(";");
					checkpoints = new HashMap<>();
					for (int i = 0; i < arrayCheckpoints.length; ) {
						checkpoints.put(new Integer(arrayCheckpoints[i++]), Sha256Hash.wrap(arrayCheckpoints[i++]));
					}
				}
				
				String delimitedDnsSeeds = properties.getProperty("dnsSeeds");
				if (delimitedDnsSeeds != null) {
					dnsSeeds = delimitedDnsSeeds.split(";");
				}
				
				
				String delimitedHttpSeeds = properties.getProperty("httpSeeds");
				if (delimitedHttpSeeds != null) {
					String [] arrayHttpSeeds = delimitedHttpSeeds.split(";");
					httpSeeds = new HttpDiscovery.Details[] {
			                new HttpDiscovery.Details(
			                        ECKey.fromPublicOnly(Utils.HEX.decode(arrayHttpSeeds[0])),
			                        URI.create(arrayHttpSeeds[1])
			                )
					};
				}
				
				
				String delimitedAddressSeeds = properties.getProperty("addressSeeds");
				if (delimitedAddressSeeds != null) {
					String[] arrayAddressSeeds = delimitedAddressSeeds.split(";");
					addressSeeds = new int[arrayAddressSeeds.length];
					for (int i = 0; i < arrayAddressSeeds.length; ++i) {
						addressSeeds[i] = new Integer(arrayAddressSeeds[i]);
					}
				}
				
				String stringTargetTimespan = properties.getProperty("targetTimespan");
				if (stringTargetTimespan != null) {
					String [] arrayTargetTimespan = stringTargetTimespan.split("\\*");
					for (String operand : arrayTargetTimespan) {
						targetTimespan = 1 * Integer.parseInt(operand);
					}
				}
				
			} catch (IOException e) {
			}
			
			
			instance = new WorkingNetworkParameters(networkId, port, paymentProtocol,
													interval, targetTimespan, maxTarget,
													dumpedPrivateKeyHeader, addressHeader,
													p2shHeader, 
													segwitAddressHrp,
													packetMagic,
													bip32HeaderP2PKHpub, bip32HeaderP2PKHpriv, 
													bip32HeaderP2WPKHpub, bip32HeaderP2WPKHpriv,
													majorityEnforceBlockUpgrade, majorityRejectBlockOutdated, majorityWindow,
													subsidyDecreaseBlockCount, spendableCoinbaseDepth,
													genesisBlock,
													checkpoints,
													dnsSeeds,
													httpSeeds,
													addressSeeds,
													genesisBlockHashCheck);
		}
		return instance;
	}
	    
	private WorkingNetworkParameters(final String networkId, final int port, final String paymentProtocol,
									final int interval, final int targetTimespan, final BigInteger maxTarget,
									final int dumpedPrivateKeyHeader, final int addressHeader,
									final int p2shHeader, 
									final String segwitAddressHrp,
									final long packetMagic,
									final int bip32HeaderP2PKHpub, final int bip32HeaderP2PKHpriv, 
									final int bip32HeaderP2WPKHpub, final int bip32HeaderP2WPKHpriv,
									final int majorityEnforceBlockUpgrade, final int majorityRejectBlockOutdated, final int majorityWindow,
									final int subsidyDecreaseBlockCount, final int spendableCoinbaseDepth,
									final Block genesisBlock,
									final Map<Integer, Sha256Hash> checkpoints,
									final String[] dnsSeeds,
									final HttpDiscovery.Details[] httpSeeds,
									final int[] addressSeeds,
									String genesisBlockHashCheck) {
		super();
       
		this.id = networkId;
		this.port = port;
		this.paymentProtocol = paymentProtocol;
		this.interval = interval;
		this.targetTimespan = targetTimespan;
		this.maxTarget = maxTarget;
		this.dumpedPrivateKeyHeader = dumpedPrivateKeyHeader;
		this.addressHeader = addressHeader;
		this.p2shHeader = p2shHeader;
		this.segwitAddressHrp = segwitAddressHrp;
		this.packetMagic = packetMagic;
		this.bip32HeaderP2PKHpub = bip32HeaderP2PKHpub;
		this.bip32HeaderP2PKHpriv = bip32HeaderP2PKHpriv;
		this.bip32HeaderP2WPKHpub = bip32HeaderP2WPKHpub;
		this.majorityEnforceBlockUpgrade = majorityRejectBlockOutdated;
		this.majorityWindow = majorityWindow;
		this.subsidyDecreaseBlockCount = subsidyDecreaseBlockCount;
		this.spendableCoinbaseDepth = spendableCoinbaseDepth;
		this.checkpoints = checkpoints;
		this.dnsSeeds = dnsSeeds;
		this.httpSeeds = httpSeeds;
		this.addrSeeds = addressSeeds;
		
		if (genesisBlock != null) {
			this.genesisBlock = genesisBlock;
		}
		
		if (genesisBlockHashCheck != null) {
			checkState(this.genesisBlock.getHashAsString().equals(genesisBlockHashCheck),
					this.genesisBlock.getHashAsString());
		} else {
			this.id = null;
			this.port = -1;
		}
	}
	
	public WorkingNetworkParameters() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getPaymentProtocolId() {
		return this.paymentProtocol;
	}

}
