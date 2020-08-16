package fedata.gcnwii.fe9.scripting;

public class PushArrayRefItem8Instruction extends ScriptInstruction {
	
	int pointerVariable;
	
	public PushArrayRefItem8Instruction(byte[] arg) {
		pointerVariable = (arg[0] & 0xFF);
	}
	
	public PushArrayRefItem8Instruction(int pointerVariable) {
		this.pointerVariable = (pointerVariable & 0xFF);
	}

	@Override
	public String displayString() {
		return "PUSH_ARRAY_REF_ITEM_8 (0x" + Integer.toHexString(pointerVariable) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x5, (byte)(pointerVariable & 0xFF) };
	}

	@Override
	public byte opcode() {
		return 0x5;
	}

	@Override
	public int numArgBytes() {
		return 1;
	}

}
