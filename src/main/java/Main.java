import api.CreateSignParams;
import api.TxCreator;
import api.TxUtils;
import api.Utxo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        /*
        try {

            KeyPairGenerator g = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec kpgparams = new ECGenParameterSpec("secp256r1");
            g.initialize(kpgparams);

            KeyPair pair = g.generateKeyPair();
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();

            String str = privateKey.toString();
            byte[] a  = privateKey.getEncoded();


            DumpedPrivateKey dumpedPrivateKey = null;
            dumpedPrivateKey = new DumpedPrivateKey(new NetworkParameters(), "L4RmQvd6PVzBTgYLpYagknNjhZxsHBbJq4ky7Zd3vB7AguSM7gF1", false);

            ECKey ecKey = dumpedPrivateKey.getKey();

            Helper.getPrivateKey(ecKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        //NeoTransfer();
        Nep5TransferWithoutFee();
    }

    public static String NeoTransfer() {
        CreateSignParams createSignParams = new CreateSignParams();
        createSignParams.setVersion((byte)1);
        createSignParams.setPriKey("L4RmQvd6PVzBTgYLpYagknNjhZxsHBbJq4ky7Zd3vB7AguSM7gF1");
        createSignParams.setFrom("ARbjp1wPh5XJchZpSjqHzGVQnnpTxNR1x7");
        createSignParams.setTo("APxpKoFCfBk8RjkRdKwyUnsBntDRXLYAZc");
        createSignParams.setAssetId("c56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b");
        createSignParams.setValue(100000000);

        Utxo utxo = Utxo.NewUtxo("b80f65fc5c0cc9a24ae2d613770202aae95dfa598f6541f75987b747eb5ca830",
                "c56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b",
                10000000000L, (short) 0);
        createSignParams.AddUtxo(utxo);

        String raw = TxCreator.createContractTransaction(createSignParams);
        return raw;
    }

    public static String Nep5TransferWithFee() {
        CreateSignParams createSignParams = new CreateSignParams();
        createSignParams.setVersion((byte)1);
        createSignParams.setPriKey("KxHLAM6z6QUfEHcxQRsCyuKLYfiU7m4EwkLTLUAPcNAvR58YBPxm");
        createSignParams.setAssetId("093430c81a5be01047263095f564d6b56b67ca7c");
        createSignParams.setFrom("AHZFHWfe3URjXhAzzJGhnrtGHCUPxJwk8V");
        createSignParams.setTo("AYNJrhY8ip1NTeECo2SAsmMdFmDqiJFsMv");
        createSignParams.setValue(100000000L);
        createSignParams.setFee(1L);

        Utxo utxo = Utxo.NewUtxo("566ebf5da9f0bcfa27811266ab62b39fe44e900c1bf166aa485e6df8378e5a80",
                TxUtils.NeoUtilityToken,
                2000L, (short) 0);
        createSignParams.AddUtxo(utxo);

        String raw = TxCreator.createInvocationTransaction(createSignParams);
        System.out.println("Invocation Tx: " + raw);

        return raw;
    }

    public static String Nep5TransferWithoutFee() {
        CreateSignParams createSignParams = new CreateSignParams();
        createSignParams.setVersion((byte)1);
        createSignParams.setPriKey("private-key");
        createSignParams.setAssetId("ecf33479dadde66b721a0791ac03e3d06bb137ab");
        createSignParams.setFrom("AJF1nLdSHbAtYZArTNRpC3qhRnJM1bCxev");
        createSignParams.setTo("ARUB61ysG7LLjuChqfjTBUNg6UB6gj4Mat");
        createSignParams.setValue(1L);
        createSignParams.setFee(0L);

        String raw = TxCreator.createInvocationTransaction(createSignParams);
        System.out.println("Invocation Tx: " + raw);
        return raw;
    }
}
