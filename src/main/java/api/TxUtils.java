package api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Random;
import java.util.Date;
import core.Utils;
import jdk.internal.org.objectweb.asm.Opcodes;
import neo.Helper;
import neo.OpCode;
import neo.ScriptBuilder;

import java.math.BigInteger;

public class TxUtils {
    public static final String NeoGovernToken  = "c56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b";
    public static final String NeoUtilityToken  = "602c79718b16e442de58778e148d0b1084e3b2dffd5de6b7b16cee7969282de7";

    public static byte[] MakeNep5Transfer(String scriptAddress, String from, String to, BigInteger value) {
        ScriptBuilder scriptBuilder = new ScriptBuilder();
        Random r = new Random(System.currentTimeMillis());
        BigInteger n = BigInteger.valueOf(r.nextLong());
        scriptBuilder.EmitPushNumber(n);
        scriptBuilder.Emit(OpCode.DROP, null);
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
