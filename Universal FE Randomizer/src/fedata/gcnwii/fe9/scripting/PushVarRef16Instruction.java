package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;
import util.WhyDoesJavaNotHaveThese;

public class PushVarRef16Instruction extends ScriptInstruction {
	
	int variableNumber;
	
	public PushVarRef16Instruction(byte[] arg) {
		variableNumber = (int)(WhyDoesJavaNotHaveThese.longValueFromByteArray(arg, false) & 0xFFFF);
	}
	
	public PushVarRef16Instruction(int variableNumber) {
		this.variableNumber = (variableNumber & 0xFFFF);
	}

	@Override
	public String displayString() {
		return "PUSH_VAR_REF_16 (0x" + Integer.toHexString(variableNumber) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x8, (byte)((variableNumber & 0xFF00) >> 8), (byte)(variableNumber & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x8;
	}

	@Override
	public int numArgBytes() {
		return 2;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new PushVarRef16Instruction(args);
	}

}
