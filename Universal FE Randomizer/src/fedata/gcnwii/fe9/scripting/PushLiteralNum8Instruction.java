package fedata.gcnwii.fe9.scripting;

public class PushLiteralNum8Instruction extends ScriptInstruction {
	
	int literal;
	
	public PushLiteralNum8Instruction(byte[] arg) {
		literal = arg[0] & 0xFF;
	}
	
	public PushLiteralNum8Instruction(int literal) {
		this.literal = literal & 0xFF;
	}

	@Override
	public String displayString() {
		return "PUSH_NUM_LITERAL_8 (0x" + Integer.toHexString(literal) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x19, (byte)(literal & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x19;
	}

	@Override
	public int numArgBytes() {
		return 1;
	}

}
