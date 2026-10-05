package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class PushArrayRefItemRef8Instruction extends ScriptInstruction {
	
	int pointerVariable;
	
	public PushArrayRefItemRef8Instruction(byte[] arg) {
		pointerVariable = arg[0] & 0xFF;
	}
	
	public PushArrayRefItemRef8Instruction(int pointerVariable) {
		this.pointerVariable = (pointerVariable & 0xFF);
	}

	@Override
	public String displayString() {
		return "PUSH_ARRAY_REF_ITEM_REF_8 (0x" + Integer.toHexString(pointerVariable) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0xB, (byte)(pointerVariable & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0xB;
	}

	@Override
	public int numArgBytes() {
		return 1;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new PushArrayRefItemRef8Instruction(args);
	}

}
