package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;
import util.WhyDoesJavaNotHaveThese;

public class PushGlobalVarRef16Instruction extends ScriptInstruction {

	int globalVariableNumber;
	
	public PushGlobalVarRef16Instruction(byte[] arg) {
		globalVariableNumber = (int)(WhyDoesJavaNotHaveThese.longValueFromByteArray(arg, false) & 0xFFFF);
	}
	
	public PushGlobalVarRef16Instruction(int globalVariableNumber) {
		this.globalVariableNumber = (globalVariableNumber & 0xFFFF);
	}
	
	@Override
	public String displayString() {
		return "PUSH_GLOBAL_VAR_REF_16 (0x" + Integer.toHexString(globalVariableNumber) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x14, (byte)((globalVariableNumber & 0xFF00) >> 8), (byte)(globalVariableNumber & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x14;
	}

	@Override
	public int numArgBytes() {
		return 2;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new PushGlobalVarRef16Instruction(args);
	}

}
