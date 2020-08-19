package org.tron.common.runtime.vm;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;
import org.testng.Assert;
import org.tron.common.runtime.TVMTestResult;
import org.tron.common.runtime.TvmTestUtils;
import org.tron.common.utils.WalletUtil;
import org.tron.core.exception.ContractExeException;
import org.tron.core.exception.ContractValidateException;
import org.tron.core.exception.ReceiptCheckErrException;
import org.tron.core.exception.VMIllegalException;
import org.tron.core.vm.config.ConfigLoader;
import org.tron.core.vm.config.VMConfig;
import org.tron.protos.Protocol;
import stest.tron.wallet.common.client.utils.AbiUtil;

@Slf4j
public class UnStakeTest extends VMContractTestBase {

  /*pragma solidity ^0.5.0;

  contract HelloWorld{
      function stakeTest(address sr, uint256 amount) public returns (bool) {
          return stake(sr, amount);
      }
      function unstakeTest() public public returns (bool) {
          return unstake();
      }
      function withdrawRewardTest() public returns (uint) {
          return withdrawreward();
      }
      function rewardBalanceTest(address addr) public returns (uint) {
          return addr.rewardbalance;
      }
  }*/

  @Test
  public void testUnstake() throws ContractExeException,
          ReceiptCheckErrException, VMIllegalException, ContractValidateException {
    ConfigLoader.disable = true;
    VMConfig.initAllowTvmTransferTrc10(1);
    VMConfig.initAllowTvmConstantinople(1);
    VMConfig.initAllowTvmSolidity059(1);
    VMConfig.initAllowTvmStake(1);
    manager.getDynamicPropertiesStore().saveChangeDelegation(1);
    String contractName = "unstakeTest";
    byte[] address = Hex.decode(OWNER_ADDRESS);
    String ABI = "[{\"constant\":false,\"inputs\":[{\"internalType\":\"address\",\"name\":\"addr\","
            + "\"type\":\"address\"}],\"name\":\"rewardBalanceTest\","
            + "\"outputs\":[{\"internalType\":\"uint256\","
            + "\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,"
            + "\"stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,"
            + "\"inputs\":[{\"internalType\":\"address\","
            + "\"name\":\"sr\",\"type\":\"address\"},{\"internalType\":\"uint256\",\""
            + "name\":\"amount\",\"type\":\"uint256\"}],\"name\":\"stakeTest\",\""
            + "outputs\":[{\"internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\""
            + "payable\":false,\"stateMutability\":\"nonpayable\",\"type\":\"function\"},"
            + "{\"constant\":false,\"inputs\":[],\"name\":\"unstakeTest\",\"outputs\":[{\""
            + "internalType\":\"bool\",\"name\":\"\",\"type\":\"bool\"}],\"payable\":false,\""
            + "stateMutability\":\"nonpayable\",\"type\":\"function\"},{\"constant\":false,\""
            + "inputs\":[],\"name\":\"withdrawRewardTest\",\"outputs\":[{\"internalType\":\""
            + "uint256\",\"name\":\"\",\"type\":\"uint256\"}],\"payable\":false,"
            + "\"stateMutability\":\"nonpayable\",\"type\":\"function\"}]";
    String factoryCode = "608060405234801561001057600080fd5b50d3801561001d57600080fd5b"
            + "50d2801561002a57600080fd5b506102018061003a6000396000f3fe608060405234801"
            + "561001057600080fd5b50d3801561001d57600080fd5b50d2801561002a57600080fd5b"
            + "50600436106100665760003560e01c8063a223c65f1461006b578063b3e835e1146100c"
            + "3578063c290120a146100e5578063e49de2d014610103575b600080fd5b6100ad600480"
            + "3603602081101561008157600080fd5b81019080803573fffffffffffffffffffffffff"
            + "fffffffffffffff169060200190929190505050610169565b6040518082815260200191"
            + "505060405180910390f35b6100cb61018a565b604051808215151515815260200191505"
            + "060405180910390f35b6100ed610192565b604051808281526020019150506040518091"
            + "0390f35b61014f6004803603604081101561011957600080fd5b81019080803573fffff"
            + "fffffffffffffffffffffffffffffffffff169060200190929190803590602001909291"
            + "9050505061019a565b604051808215151515815260200191505060405180910390f35b6"
            + "0008173ffffffffffffffffffffffffffffffffffffffff16d89050919050565b6000d6"
            + "905090565b6000d7905090565b60008183d590509291505056fea26474726f6e58204ba"
            + "2e62e49bd76cb49dd0e1ce7be45dcae5f764ea02b1a11a34c7ef83ca3362764736f6c63"
            + "7828302e352e31332d646576656c6f702e323032302e382e31332b636f6d6d69742e376"
            + "33236393863300057";
    long value = 0;
    long fee = 100000000;
    long consumeUserResourcePercent = 0;
    // deploy contract
    Protocol.Transaction trx = TvmTestUtils.generateDeploySmartContractAndGetTransaction(
            contractName, address, ABI, factoryCode, value, fee, consumeUserResourcePercent,
            null);
    byte[] factoryAddress = WalletUtil.generateContractAddress(trx);
    runtime = TvmTestUtils.processTransactionAndReturnRuntime(trx, manager, null);
    Assert.assertNull(runtime.getRuntimeError());

    rootRepository.addBalance(factoryAddress, 10000000000L);
    rootRepository.commit();
    Assert.assertEquals(rootRepository.getBalance(factoryAddress), 10000000000L);

    manager.getDynamicPropertiesStore().saveMinFrozenTime(0);

    // Trigger contract method: stakeTest(address,uint256)
    String stakeTest = "stakeTest(address,uint256)";
    String witness = "27Ssb1WE8FArwJVRRb8Dwy3ssVGuLY8L3S1";
    String hexInput = AbiUtil.parseMethod(stakeTest, Arrays.asList(witness, 100000000));
    TVMTestResult result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, null);
    Assert.assertNull(result.getRuntime().getRuntimeError());
    byte[] returnValue = result.getRuntime().getResult().getHReturn();
    Assert.assertEquals(Hex.toHexString(returnValue),
            "0000000000000000000000000000000000000000000000000000000000000001");

    //vote
    String unstakeTest = "unstakeTest()";
    hexInput = AbiUtil.parseMethod(unstakeTest, Arrays.asList(""));
    result = TvmTestUtils
            .triggerContractAndReturnTvmTestResult(Hex.decode(OWNER_ADDRESS),
                    factoryAddress, Hex.decode(hexInput), 0, fee, manager, null);
    Assert.assertNull(result.getRuntime().getRuntimeError());
    returnValue = result.getRuntime().getResult().getHReturn();
    Assert.assertEquals(Hex.toHexString(returnValue),
            "0000000000000000000000000000000000000000000000000000000000000001");
  }
}
