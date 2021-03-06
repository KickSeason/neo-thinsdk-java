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
        Nep5Transfer();
    }

    public static String NeoTransfer() {
        CreateSignParams createSignParams = new CreateSignParams();
        createSignParams.setVersion((byte)1);
        createSignParams.setPriKey("L4RmQvd6PVzBTgYLpYagknNjhZxsHBbJq4ky7Zd3vB7AguSM7gF1");
        createSignParams.setFrom("ARbjp1wPh5XJchZpSjqHzGVQnnpTxNR1x7");
        createSignParams.setTo("APxpKoFCfBk8RjkRdKwyUnsBntDRXLYAZc");
        createSignParams.setAssetId("c56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b");
        createSignParams.setValue(100000000);

        List<Utxo> utxoList = new ArrayList<>();
        Utxo utxo = new Utxo();
        utxo.setHash("b80f65fc5c0cc9a24ae2d613770202aae95dfa598f6541f75987b747eb5ca830");
        utxo.setValue(10000000000L);
        utxo.setN((short) 0);
        utxoList.add(utxo);

        createSignParams.setUtxos(utxoList);

        String raw = TxCreator.createContractTransaction(createSignParams);
        return raw;
    }

    public static String Nep5Transfer() {
        CreateSignParams createSignParams = new CreateSignParams();
        createSignParams.setVersion((byte)1);
        createSignParams.setPriKey("KxHLAM6z6QUfEHcxQRsCyuKLYfiU7m4EwkLTLUAPcNAvR58YBPxm");
        createSignParams.setFrom("AHZFHWfe3URjXhAzzJGhnrtGHCUPxJwk8V");

        BigInteger value = new BigInteger("200000000");
        byte[] data = TxUtils.MakeNep5Transfer("093430c81a5be01047263095f564d6b56b67ca7c", "AHZFHWfe3URjXhAzzJGhnrtGHCUPxJwk8V", "AYNJrhY8ip1NTeECo2SAsmMdFmDqiJFsMv", value);
        createSignParams.setData(data);

        String raw = TxCreator.createInvocationTransactionWithoutFee(createSignParams);
        System.out.println("Invocation Tx: " + raw);

        return raw;
    }
}
