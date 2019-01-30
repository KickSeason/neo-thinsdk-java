package api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import core.Utils;
import neo.Helper;
import neo.OpCode;
import neo.ScriptBuilder;

import java.math.BigInteger;

public class TxUtils {
    public static byte[] MakeNep5Transfer(String scriptAddress, String from, String to, BigInteger value) {
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
