package fedata.gcnwii.fe9.scripting;

public class PushGlobalArrayVarItem8Instruction extends ScriptInstruction {
	
	int globalVariableNumber;
	
	public PushGlobalArrayVarItem8Instruction(byte[] arg) {
		globalVariableNumber = (arg[0] & 0xFF);
	}
	
	public PushGlobalArrayVarItem8Instruction(int globalVariableNumber) {
		this.globalVariableNumber = (globalVariableNumber & 0xFF);
	}

	@Override
	public String displayString() {
		return "PUSH_GLOBAL_ARRAY_VAR_ITEM_8 (0x" + Integer.toHexString(globalVariableNumber) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0xF, (byte)(globalVariableNumber & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0xF;
	}

	@Override
	public int numArgBytes() {
		return 1;
	}
}
