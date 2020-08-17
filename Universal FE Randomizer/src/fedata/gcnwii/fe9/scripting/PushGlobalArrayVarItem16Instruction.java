package fedata.gcnwii.fe9.scripting;

import util.WhyDoesJavaNotHaveThese;

public class PushGlobalArrayVarItem16Instruction extends ScriptInstruction {

	int globalVariableNumber;
	
	public PushGlobalArrayVarItem16Instruction(byte[] arg) {
		globalVariableNumber = (int)(WhyDoesJavaNotHaveThese.longValueFromByteArray(arg, false) & 0xFFFF);
	}
	
	public PushGlobalArrayVarItem16Instruction(int globalVariableNumber) {
		this.globalVariableNumber = (globalVariableNumber & 0xFFFF);
	}
	
	@Override
	public String displayString() {
		return "PUSH_GLOBAL_ARRAY_VAR_ITEM_16 (0x" + Integer.toHexString(globalVariableNumber) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x10, (byte)((globalVariableNumber & 0xFF00) >> 8), (byte)(globalVariableNumber & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x10;
	}

	@Override
	public int numArgBytes() {
		return 2;
	}

}
