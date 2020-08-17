package fedata.gcnwii.fe9.scripting;

public class PushGlobalVarRef8Instruction extends ScriptInstruction {
	
	int globalVariableNumber;
	
	public PushGlobalVarRef8Instruction(byte[] arg) {
		globalVariableNumber = arg[0] & 0xFF;
	}
	
	public PushGlobalVarRef8Instruction(int globalVariableNumber) {
		this.globalVariableNumber = globalVariableNumber & 0xFF;
	}

	@Override
	public String displayString() {
		return "PUSH_GLOBAL_VAR_REF_8 (0x" + Integer.toHexString(globalVariableNumber) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x13, (byte)(globalVariableNumber & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x13;
	}

	@Override
	public int numArgBytes() {
		return 1;
	}

}
