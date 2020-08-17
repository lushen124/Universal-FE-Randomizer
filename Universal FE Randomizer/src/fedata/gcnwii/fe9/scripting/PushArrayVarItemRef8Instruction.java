package fedata.gcnwii.fe9.scripting;

public class PushArrayVarItemRef8Instruction extends ScriptInstruction {

	int arrayVariable;
	
	public PushArrayVarItemRef8Instruction(byte[] arg) {
		arrayVariable = arg[0] & 0xFF;
	}
	
	public PushArrayVarItemRef8Instruction(int arrayVariable) {
		this.arrayVariable = arrayVariable & 0xFF;
	}
	
	@Override
	public String displayString() {
		return "PUSH_ARRAY_VAR_ITEM_REF_8 (0x" + Integer.toHexString(arrayVariable) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x9, (byte)(arrayVariable & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x9;
	}

	@Override
	public int numArgBytes() {
		return 1;
	}

}
