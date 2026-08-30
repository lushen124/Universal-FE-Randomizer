package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;
import util.WhyDoesJavaNotHaveThese;

public class PushVar16Instruction extends ScriptInstruction {

	int variableNumber;
	
	public PushVar16Instruction(byte[] arg) {
		variableNumber = (int)(WhyDoesJavaNotHaveThese.longValueFromByteArray(arg, false) & 0xFFFF);
	}
	
	public PushVar16Instruction(int variableNumber) {
		this.variableNumber = variableNumber & 0xFFFF;
	}
	
	@Override
	public String displayString() {
		return "PUSH_VAR_16 (0x" + Integer.toHexString(variableNumber) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x2, (byte)((variableNumber & 0xFF00) >> 8), (byte)(variableNumber & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x2;
	}

	@Override
	public int numArgBytes() {
		return 2;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new PushVar16Instruction(args);
	}

}
