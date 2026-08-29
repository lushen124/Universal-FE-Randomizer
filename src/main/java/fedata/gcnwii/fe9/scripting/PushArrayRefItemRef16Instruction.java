package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;
import util.WhyDoesJavaNotHaveThese;

public class PushArrayRefItemRef16Instruction extends ScriptInstruction {

	int pointerVariable;
	
	public PushArrayRefItemRef16Instruction(byte[] arg) {
		pointerVariable = (int)(WhyDoesJavaNotHaveThese.longValueFromByteArray(arg, false) & 0xFFFF);
	}
	
	public PushArrayRefItemRef16Instruction(int pointerVariable) {
		this.pointerVariable = (pointerVariable & 0xFFFF);
	}
	
	@Override
	public String displayString() {
		return "PUSH_ARRAY_REF_ITEM_REF_16 (0x" + Integer.toHexString(pointerVariable) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0xC, (byte)((pointerVariable & 0xFF00) >> 8), (byte)(pointerVariable & 0xFF) };
	}

	@Override
	public byte opcode() {
		return 0xC;
	}

	@Override
	public int numArgBytes() {
		return 2;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new PushArrayRefItemRef16Instruction(args);
	}

}
