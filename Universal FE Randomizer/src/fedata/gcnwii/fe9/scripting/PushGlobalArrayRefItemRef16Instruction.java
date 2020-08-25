package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;
import util.WhyDoesJavaNotHaveThese;

public class PushGlobalArrayRefItemRef16Instruction extends ScriptInstruction {

	int globalPointerVariable;
	
	public PushGlobalArrayRefItemRef16Instruction(byte[] arg) {
		globalPointerVariable = (int)(WhyDoesJavaNotHaveThese.longValueFromByteArray(arg, false) & 0xFFFF);
	}
	
	public PushGlobalArrayRefItemRef16Instruction(int globalPointerVariable) {
		this.globalPointerVariable = (globalPointerVariable & 0xFFFF);
	}
	
	@Override
	public String displayString() {
		return "PUSH_GLOBAL_ARRAY_REF_ITEM_REF_16 (0x" + Integer.toHexString(globalPointerVariable) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x18, (byte)((globalPointerVariable & 0xFF00) >> 8), (byte)(globalPointerVariable & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x18;
	}

	@Override
	public int numArgBytes() {
		return 2;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new PushGlobalArrayRefItemRef16Instruction(args);
	}

}
