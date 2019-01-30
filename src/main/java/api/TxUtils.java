package api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import core.Utils;
import neo.Helper;
import neo.OpCode;
import neo.ScriptBuilder;

import java.math.BigInteger;

public class TxUtils {
    public static byte[] makeNep5Transfer(String scriptAddress, String from, String to, BigInteger value) {
        byte[] assetId = Utils.hexStringToBytes(scriptAddress);
        assetId = Utils.reverseBytes(assetId);

        String fromParam = "(address)" + from;
        JsonObject fromJson = new JsonObject();
        fromJson.addProperty("from", fromParam);

        String toParam = "(address)" + to;
        JsonObject toJson = new JsonObject();
        toJson.addProperty("to", toParam);

        String numParam = "(integer)" + value.toString();
        JsonObject numJson = new JsonObject();
        numJson.addProperty("num", numParam);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(numParam);
        jsonArray.add(toJson);
        jsonArray.add(fromJson);

        ScriptBuilder scriptBuilder = new ScriptBuilder();
        scriptBuilder.EmitParamJson(jsonArray);
        scriptBuilder.EmitPushString("transfer");
        try {
            scriptBuilder.EmitAppCall(assetId, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return scriptBuilder.toBytes();
    }

    public static byte[] MakeNep5Transfer2(String scriptAddress, String from, String to, BigInteger value) {
        ScriptBuilder scriptBuilder = new ScriptBuilder();
        scriptBuilder.EmitPushBytes(Utils.reverseBytes(value.toByteArray()));
        scriptBuilder.EmitPushBytes(Helper.getPublicKeyHashFromAddress(to));
        scriptBuilder.EmitPushBytes(Helper.getPublicKeyHashFromAddress(from));
        scriptBuilder.Emit(OpCode.PUSH3, null);
        scriptBuilder.Emit(OpCode.PACK, null);
        scriptBuilder.EmitPushString("transfer");
        try {
            scriptBuilder.EmitAppCall(Utils.reverseBytes(Utils.hexStringToBytes(scriptAddress)), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scriptBuilder.toBytes();
    }
}
