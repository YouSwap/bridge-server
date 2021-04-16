package you.chain;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.RandomStringUtils;
import org.bitcoinj.crypto.*;
import org.bitcoinj.wallet.DeterministicSeed;
import org.springframework.boot.SpringApplication;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;
import sun.security.provider.SecureRandom;
import you.BridgeServerApplication;
import you.chain.utils.TransferModel;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.*;

public class ApiChain {

    /**
     * path路径
     */
    private final static ImmutableList<ChildNumber> BIP44_ETH_ACCOUNT_ZERO_PATH =
            ImmutableList.of(new ChildNumber(44, true), new ChildNumber(60, true),
                    ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);
    /**
     * 查询最新区块高度
     *
     * @return
     */
    public static BigInteger getBlockHeight(Web3j chainRpc) {
        BigInteger newBlockHeight = null;
        try {
            newBlockHeight = chainRpc.ethBlockNumber().send().getBlockNumber();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newBlockHeight;

    }

    /**
     * 获取区块中的交易数据
     *
     * @return
     */
    public static EthBlock.Block getBlockData(BigInteger blockHeight, Web3j chainRpc) {
        DefaultBlockParameter blockNumber = DefaultBlockParameter.valueOf(blockHeight);
        try {
            return chainRpc.ethGetBlockByNumber(blockNumber, true).send().getBlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询交易数据详情
     *
     * @param hash
     * @return
     */
    public static TransactionReceipt getTransactionData(String hash, Web3j chainRpc) {
        try {
            EthGetTransactionReceipt send = chainRpc.ethGetTransactionReceipt(hash).send();
            Optional<TransactionReceipt> transactionReceipt = send.getTransactionReceipt();
            if (!transactionReceipt.isPresent()) {
                return null;
            }
            return transactionReceipt.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 数据转码
     *
     * @param args
     * @param type
     * @return
     */
    public static String getRefMethod(String args, String type) {
        try {
            Method refMethod = TypeDecoder.class.getDeclaredMethod("decode", String.class, int.class, Class.class);
            refMethod.setAccessible(true);
            if (type.equalsIgnoreCase("address")) {
                Address value = (Address) refMethod.invoke(null, args, 0, Address.class);
                return value.getValue();
            } else if (type.equalsIgnoreCase("uint")) {
                Uint256 value = (Uint256) refMethod.invoke(null, args, 0, Uint256.class);
                return value.getValue().toString();
            } else if (type.equalsIgnoreCase("string")) {
                Utf8String value = (Utf8String) refMethod.invoke(null, args, 0, Utf8String.class);
                return value.getValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询地址Nonce
     *
     * @param address
     */
    public static BigInteger getNonce(String address, Web3j chainRpc) {
        //查询地址交易编号
        try {
            return chainRpc.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 签名广播交易
     * @param method
     * @param tran
     * @param inputArgs
     * @param chainRpc
     * @return
     * @throws Exception
     */
    public static Map<String, Object> signSendTransaction(String method, TransferModel tran, List<Type> inputArgs, Web3j chainRpc){
        try {
            Map<String, Object> maps = new HashMap<>();
            List<TypeReference<?>> outputArgs = new ArrayList<>();
            //构造交易数据
            String funcData = FunctionEncoder.encode(new Function(method, inputArgs, outputArgs));
            Credentials credentials = Credentials.create(tran.getPrivateKey());
            //签名交易
            RawTransaction rawTransaction = RawTransaction.createTransaction(tran.getNonce(), tran.getGasPrice(), tran.getGasLimit(), tran.getContractAddress(), funcData);
            byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            //广播交易
            EthSendTransaction ethSendTransaction = chainRpc.ethSendRawTransaction(Numeric.toHexString(signMessage)).sendAsync().get();
            if (ethSendTransaction.getError() != null) {
                String message = ethSendTransaction.getError().getMessage();
                System.out.println("message=="+message);
                boolean status = message.contains("nonce too low");
                if(status){
                    maps.put("status", "nonce");
                    maps.put("hash", "0x0");
                }else{
                    maps.put("status", "msg");
                    maps.put("hash", message);
                }
            }else{
                System.out.println(method+"===="+ethSendTransaction.getTransactionHash());
                maps.put("funcData", funcData);
                maps.put("signData", Numeric.toHexString(signMessage));
                maps.put("hash", ethSendTransaction.getTransactionHash());
                maps.put("status", "true");
            }
            return maps;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 新建地址
     *
     * @return 地址业务实体
     */
    public static Map<String, Object> createdAddress() {
        try{
            Map<String, Object> maps = new HashMap<>();
            SecureRandom secureRandom = new SecureRandom();
            byte[] entropy = new byte[DeterministicSeed.DEFAULT_SEED_ENTROPY_BITS / 8];
            secureRandom.engineNextBytes(entropy);
            //生成12位助记词
            List<String> str = MnemonicCode.INSTANCE.toMnemonic(entropy);
            //使用助记词生成钱包种子
            byte[] seed = MnemonicCode.toSeed(str, "");
            DeterministicKey masterPrivateKey = HDKeyDerivation.createMasterPrivateKey(seed);
            DeterministicHierarchy deterministicHierarchy = new DeterministicHierarchy(masterPrivateKey);
            DeterministicKey deterministicKey = deterministicHierarchy
                    .deriveChild(BIP44_ETH_ACCOUNT_ZERO_PATH, false, true, new ChildNumber(0));
            byte[] bytes = deterministicKey.getPrivKeyBytes();
            ECKeyPair keyPair = ECKeyPair.create(bytes);
            //通过公钥生成钱包地址
            String address = "0x"+Keys.getAddress(keyPair.getPublicKey());
            String privateKey ="0x"+keyPair.getPrivateKey().toString(16);
            maps.put("address", address);
            maps.put("privateKey", privateKey);
            return maps;
        }catch (Exception e){
            return null;
        }

    }
}