package fedata.gcnwii.fe9.scripting;

import util.WhyDoesJavaNotHaveThese;

public class PushArrayRefItem16Instruction extends ScriptInstruction {

	int pointerVariable;
	
	public PushArrayRefItem16Instruction(byte[] arg) {
		pointerVariable = (int)(WhyDoesJavaNotHaveThese.longValueFromByteArray(arg, false) & 0xFFFF);
	}
	
	public PushArrayRefItem16Instruction(int pointerVariable) {
		this.pointerVariable = (pointerVariable & 0xFFFF);
	}
	
	@Override
	public String displayString() {
		return "PUSH_ARRAY_REF_ITEM_16 (0x" + Integer.toHexString(pointerVariable) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x6, (byte)((pointerVariable & 0xFF00) >> 8), (byte)(pointerVariable & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x6;
	}

	@Override
	public int numArgBytes() {
		return 2;
	}

}
