package fedata.gcnwii.fe9.scripting;

public class PushVar8Instruction extends ScriptInstruction{

	int variableNumber;
	
	public PushVar8Instruction(byte[] arg) {
		variableNumber = arg[0] & 0xFF;
	}
	
	public PushVar8Instruction(int variableNumber) {
		this.variableNumber = variableNumber & 0xFF;
	}
	
	@Override
	public String displayString() {
		return "PUSH_VAR_8 (0x" + Integer.toHexString(variableNumber).toUpperCase() + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x1, (byte)variableNumber};
	}

	@Override
	public byte opcode() {
		return 0x1;
	}

	@Override
	public int numArgBytes() {
		return 1;
	}

}
