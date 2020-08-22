package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;
import util.WhyDoesJavaNotHaveThese;

public class PushGlobalArrayVarItemRef16Instruction extends ScriptInstruction {

	int globalVariableNumber;
	
	public PushGlobalArrayVarItemRef16Instruction(byte[] arg) {
		globalVariableNumber = (int)(WhyDoesJavaNotHaveThese.longValueFromByteArray(arg, false) & 0xFFFF);
	}
	
	public PushGlobalArrayVarItemRef16Instruction(int globalVariableNumber) {
		this.globalVariableNumber = (globalVariableNumber & 0xFFFF);
	}
	
	@Override
	public String displayString() {
		return "PUSH_GLOBAL_ARRAY_VAR_ITEM_REF_16 (0x" + Integer.toHexString(globalVariableNumber) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x16, (byte)((globalVariableNumber & 0xFF00) >> 8), (byte)(globalVariableNumber & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x16;
	}

	@Override
	public int numArgBytes() {
		return 2;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new PushGlobalArrayVarItemRef16Instruction(args);
	}

}
