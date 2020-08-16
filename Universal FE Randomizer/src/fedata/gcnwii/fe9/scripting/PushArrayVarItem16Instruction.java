package fedata.gcnwii.fe9.scripting;

import util.WhyDoesJavaNotHaveThese;

public class PushArrayVarItem16Instruction extends ScriptInstruction {
	
	int arrayVariable;
	
	public PushArrayVarItem16Instruction(byte[] arg) {
		arrayVariable = (int)(WhyDoesJavaNotHaveThese.longValueFromByteArray(arg, false) & 0xFFFF);
	}
	
	public PushArrayVarItem16Instruction(int arrayVariable) {
		this.arrayVariable = (arrayVariable & 0xFFFF);
	}

	@Override
	public String displayString() {
		return "PUSH_ARRAY_VAR_ITEM_16 (0x" + Integer.toHexString(arrayVariable) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x4, (byte)((arrayVariable & 0xFF00) >> 8), (byte)(arrayVariable & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x4;
	}

	@Override
	public int numArgBytes() {
		return 2;
	}

}
