package fedata.gcnwii.fe9.scripting;

public class PushGlobalArrayVarItemRef8Instruction extends ScriptInstruction {

	int globalVariableNumber;
	
	public PushGlobalArrayVarItemRef8Instruction(byte[] arg) {
		globalVariableNumber = arg[0] & 0xFF;
	}
	
	public PushGlobalArrayVarItemRef8Instruction(int globalVariableNumber) {
		this.globalVariableNumber = globalVariableNumber & 0xFF;
	}
	
	@Override
	public String displayString() {
		return "PUSH_GLOBAL_ARRAY_VAR_ITEM_REF_8 (0x" + Integer.toHexString(globalVariableNumber) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x15, (byte)(globalVariableNumber & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x15;
	}

	@Override
	public int numArgBytes() {
		return 1;
	}

}
