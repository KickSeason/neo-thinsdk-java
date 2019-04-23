package api;

import lombok.Data;

@Data
public class Utxo {
    private String hash;
    private String assetId;
    private long value;
    private short index;

    public static Utxo NewUtxo(String hash, String assetid, long value, short index) {
        Utxo utxo = new Utxo();
        utxo.setHash(hash);
        utxo.setAssetId(assetid);
        utxo.setValue(value);
        utxo.setIndex(index);
        return utxo;
    }
}
