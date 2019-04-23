package api;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateSignParams {
    private byte txType;
    private byte version;
    private String priKey;
    private String from;
    private String to;
    private String assetId;
    private long value;
    private long fee;
//    private byte[] data;
    private List<Utxo> utxos;

    public CreateSignParams() {
        this.utxos = new ArrayList<>();
    }
    public boolean AddUtxo(Utxo utxo) {
        if (this.utxos.contains(utxo)) {
            return false;
        }
        this.utxos.add(utxo);
        return true;
    }
}
