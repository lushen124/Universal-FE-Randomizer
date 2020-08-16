package fedata.gcnwii.fe9.scripting;

public class PushVarRef8Instruction extends ScriptInstruction {

	int variableNumber;
	
	public PushVarRef8Instruction(byte[] arg) {
		variableNumber = (arg[0] & 0xFF);
	}
	
	public PushVarRef8Instruction(int variableNumber) {
		this.variableNumber = (variableNumber & 0xFF);
	}
	
	@Override
	public String displayString() {
		return "PUSH_VAR_REF_8 (0x" + Integer.toHexString(variableNumber) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x7, (byte)(variableNumber & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x7;
	}

	@Override
	public int numArgBytes() {
		return 1;
	}

}
