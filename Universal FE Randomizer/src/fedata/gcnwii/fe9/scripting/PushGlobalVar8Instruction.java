package fedata.gcnwii.fe9.scripting;

public class PushGlobalVar8Instruction extends ScriptInstruction {

	int globalVariableNumber;
	
	public PushGlobalVar8Instruction(byte[] arg) {
		globalVariableNumber = arg[0] & 0xFF;
	}
	
	public PushGlobalVar8Instruction(int globalVariableNumber) {
		this.globalVariableNumber = globalVariableNumber & 0xFF;
	}
	@Override
	public String displayString() {
		return "PUSH_GLOBAL_VAR_8 (0x" + Integer.toHexString(globalVariableNumber) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0xD, (byte)(globalVariableNumber & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0xD;
	}

	@Override
	public int numArgBytes() {
		return 1;
	}

}
