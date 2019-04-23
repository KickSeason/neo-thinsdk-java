package api;

import core.*;
import neo.*;

import java.math.BigInteger;
import java.security.interfaces.ECPrivateKey;
import java.util.ArrayList;
import java.util.List;

public class TxCreator {
    public static String createContractTransaction(CreateSignParams params) {
        String fromAddress = params.getFrom();
        String toAddress = params.getTo();
        String asset = params.getAssetId();
        long value = params.getValue();
        long fee = params.getFee();
        long gassum = 0, assetsum = 0;

        Transaction tx = new Transaction();
        tx.setTxtype(TransactionType.ContractTransaction);
        tx.setVersion(params.getVersion());

        List<Utxo> utxos = params.getUtxos();
        for (int i = 0; i < utxos.size(); i ++) {
            Utxo utxo = utxos.get(i);
            if (utxo.getAssetId() == asset) {
                assetsum += utxo.getValue();
            }
            if (utxo.getAssetId() == TxUtils.NeoUtilityToken) {
                gassum += utxo.getValue();
            }
        }
        if ((asset == TxUtils.NeoUtilityToken && gassum < value + fee) || (asset != TxUtils.NeoUtilityToken && (gassum < fee || assetsum < value))) {
            System.out.println("[createContractTransaction] error: not enough utxo");
            return "";
        }
        List<TransactionInput> inputs = new ArrayList<TransactionInput>();
        for(int i = 0; i < utxos.size(); i++) {
            Utxo utxo = utxos.get(i);
            TransactionInput input = new TransactionInput();
            if (utxo.getAssetId() == asset || utxo.getAssetId() == TxUtils.NeoUtilityToken) {
                input.setIndex(utxo.getIndex());
                input.setHash(Utils.reverseBytes(Utils.hexStringToBytes(utxo.getHash())));
                inputs.add(input);
            }
        }


        List<TransactionOutput> outputs = new ArrayList<>();
        byte[] tohash = Helper.getPublicKeyHashFromAddress(toAddress);
        if (asset == TxUtils.NeoUtilityToken) {
            if (value + fee < gassum) {
                TransactionOutput output = new TransactionOutput();
                Fixed8 change = new Fixed8();
                change.setValue(gassum - value -fee);
                output.setValue(change);
                output.setAssetId(Utils.reverseBytes(Utils.hexStringToBytes(TxUtils.NeoUtilityToken)));
                output.setToAddress(tohash);
            }
        } else {
            if (fee < gassum) {
                TransactionOutput output = new TransactionOutput();
                Fixed8 gaschange = new Fixed8();
                gaschange.setValue(gassum -fee);
                output.setValue(gaschange);
                output.setAssetId(Utils.reverseBytes(Utils.hexStringToBytes(TxUtils.NeoUtilityToken)));
                output.setToAddress(tohash);
            }
            if (value < assetsum) {
                TransactionOutput output = new TransactionOutput();
                Fixed8 assetchange = new Fixed8();
                assetchange.setValue(assetsum -value);
                output.setValue(assetchange);
                output.setAssetId(Utils.reverseBytes(Utils.hexStringToBytes(asset)));
                output.setToAddress(tohash);
            }
        }


        byte[] unsignedData = tx.getMessage();
        String privKey = params.getPriKey();
        DumpedPrivateKey dumpedPrivateKey = null;

        try {
            dumpedPrivateKey = new DumpedPrivateKey(new NetworkParameters(), privKey, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(dumpedPrivateKey == null) {
            return "";
        }

        ECKey ecKey = dumpedPrivateKey.getKey();

        Sha256Hash sha256Hash = Sha256Hash.create(unsignedData);

        byte[] signature = Helper.sign(sha256Hash, ecKey);
        byte[] pub = ecKey.getPubKey();
        tx.addWitness(signature, pub, fromAddress);

        /*
        ECPrivateKey ecPrivateKey = Helper.getPrivateKey(ecKey);
        byte[] signData = null;
        try {
            signData = Helper.signature(unsignedData, ecPrivateKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        BigInteger p = ecKey.getPriv();
        byte[] pub = ECKey.publicKeyFromPrivate(p, false);
        byte[] compressed = ECKey.publicKeyFromPrivate(p, true);
        tx.addWitness(signData, pub, compressed, fromAddress);
        */

        byte[] rawData = tx.getRawData();
        String raw = Utils.bytesToHexString(rawData);

        return raw;
    }
//
//    public static String createInvocationTransactionOld(CreateSignParams params) {
//        Transaction tx = new Transaction();
//        tx.setTxtype(TransactionType.InvocationTransaction);
//        tx.setVersion(params.getVersion());
//
//        long sum = 0;
//        List<Utxo> utxos = params.getUtxos();
//
//        List<TransactionInput> inputs = null;
//        int size = utxos.size();
//        if(size > 0) {
//            inputs = new ArrayList<TransactionInput>();
//            tx.setInputs(inputs);
//        }
//
//        for(int i = 0; i < size; i++) {
//            TransactionInput input = new TransactionInput();
//            inputs.add(input);
//            Utxo utxo = utxos.get(i);
//            byte[] hash = Utils.hexStringToBytes(utxo.getHash());
//            hash = Utils.reverseBytes(hash);
//            input.setHash(hash);
//
//            input.setIndex(utxo.getN());
//            sum += utxo.getValue();
//        }
//
//        String toAddress = params.getTo();
//        if(sum <= 0) {
//            return "";
//        }
//
//        List<TransactionOutput> outputs = new ArrayList<>();
//        tx.setOutputs(outputs);
//
//        String assetId = params.getAssetId();
//        TransactionOutput output = new TransactionOutput();
//        outputs.add(output);
//        byte[] vAssetId = Utils.hexStringToBytes(assetId);
//        vAssetId = Utils.reverseBytes(vAssetId);
//        output.setAssetId(vAssetId);
//        Fixed8 value = new Fixed8();
//        value.setValue(sum);
//        output.setValue(value);
//        byte[] pubkeyhash = Helper.getPublicKeyHashFromAddress(toAddress);
//        output.setToAddress(pubkeyhash);
//
//        String fromAddress = params.getFrom();
//        InvokeTransData invokeTransData = new InvokeTransData();
//        invokeTransData.setScript(params.getData());
//        Fixed8 gas = new Fixed8();
//        gas.setValue(100000000);
//        invokeTransData.setGas(gas);
//        tx.setExtdata(invokeTransData);
//
//        byte[] unsignedData = tx.getMessage();
//        String privKey = params.getPriKey();
//        DumpedPrivateKey dumpedPrivateKey = null;
//
//        try {
//            dumpedPrivateKey = new DumpedPrivateKey(new NetworkParameters(), privKey, false);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if(dumpedPrivateKey == null) {
//            return "";
//        }
//
//        ECKey ecKey = dumpedPrivateKey.getKey();
//
//        Sha256Hash sha256Hash = Sha256Hash.create(unsignedData);
//
//        byte[] signature = Helper.sign(sha256Hash, ecKey);
//        byte[] pub = ecKey.getPubKey();
//        tx.addWitness(signature, pub, fromAddress);
//
//        /*
//        ECPrivateKey ecPrivateKey = Helper.getPrivateKey(ecKey);
//        byte[] signData = null;
//        try {
//            signData = Helper.signature(unsignedData, ecPrivateKey);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        BigInteger p = ecKey.getPriv();
//        byte[] pub = ECKey.publicKeyFromPrivate(p, false);
//        byte[] compressed = ECKey.publicKeyFromPrivate(p, true);
//        tx.addWitness(signData, pub, compressed, fromAddress);
//        */
//
//        byte[] rawData = tx.getRawData();
//        String raw = Utils.bytesToHexString(rawData);
//        return  raw;
//    }
    public static String createInvocationTransaction(CreateSignParams params) {
        String fromAddress = params.getFrom();
        String toAddress = params.getTo();
        String asset = params.getAssetId();
        long value = params.getValue();
        long fee = params.getFee();
        long sum = 0;

        Transaction tx = new Transaction();

        tx.setTxtype(TransactionType.InvocationTransaction);
        tx.setVersion(params.getVersion());

        Attribute attr = new Attribute();
        attr.setUsage(AttributeType.Script);
        try{
            Address address = new Address(fromAddress);
            attr.setData(address.getHash160());
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }
        tx.addAttribute(attr);

        List<Utxo> utxos = params.getUtxos();
        for (int i = 0; i < utxos.size(); i++) {
            Utxo utxo = utxos.get(i);
            if (utxo.getAssetId() == TxUtils.NeoUtilityToken) {
                sum += utxo.getValue();
            }
        }
        if (sum < fee) {
            System.out.println("[createInvocationTransaction] error: not enough utxo for fee");
            return "";
        }
        List<TransactionInput> inputs = new ArrayList<>();
        for (int i = 0; i < utxos.size(); i++) {
            Utxo utxo = utxos.get(i);
            TransactionInput input = new TransactionInput();
            if (utxo.getAssetId() == TxUtils.NeoUtilityToken) {
                input.setHash(Utils.reverseBytes(Utils.hexStringToBytes(utxo.getHash())));
                input.setIndex(utxo.getIndex());
                inputs.add(input);
            }
        }
        tx.setInputs(inputs);

        List<TransactionOutput> outputs = new ArrayList<>();
        if (fee < sum) {
            TransactionOutput output = new TransactionOutput();
            output.setAssetId(Utils.reverseBytes(Utils.hexStringToBytes(TxUtils.NeoUtilityToken)));
            output.setToAddress(Helper.getPublicKeyHashFromAddress(fromAddress));
            Fixed8 change = new Fixed8();
            change.setValue(sum - fee);
            output.setValue(change);
            outputs.add(output);
        }
        tx.setOutputs(outputs);

        BigInteger bgvalue = BigInteger.valueOf(value);
        if (asset == TxUtils.NeoGovernToken || asset == TxUtils.NeoUtilityToken) {
            System.out.println("[createInvocationTransaction] error: please set contract hash as assetid in invocation transaction");
            return "";
        }
        byte[] data = TxUtils.MakeNep5Transfer(asset, fromAddress, toAddress, bgvalue);

        InvokeTransData invokeTransData = new InvokeTransData();
        invokeTransData.setScript(data);
        Fixed8 gas = new Fixed8();
        gas.setValue(0);
        invokeTransData.setGas(gas);
        tx.setExtdata(invokeTransData);

        byte[] unsignedData = tx.getMessage();
        String privKey = params.getPriKey();
        DumpedPrivateKey dumpedPrivateKey = null;

        try {
            dumpedPrivateKey = new DumpedPrivateKey(new NetworkParameters(), privKey, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(dumpedPrivateKey == null) {
            return "";
        }

        ECKey ecKey = dumpedPrivateKey.getKey();

        Sha256Hash sha256Hash = Sha256Hash.create(unsignedData);

        byte[] signature = Helper.sign(sha256Hash, ecKey);
        byte[] pub = ecKey.getPubKey();
        tx.addWitness(signature, pub, fromAddress);

        byte[] rawData = tx.getRawData();
        String raw = Utils.bytesToHexString(rawData);
        return  raw;
    }

}

