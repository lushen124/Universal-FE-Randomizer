package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;
import util.WhyDoesJavaNotHaveThese;

public class BranchAndKeepIfFalseInstruction extends ScriptInstruction {

	int offset;
	
	public BranchAndKeepIfFalseInstruction(byte[] arg) {
		offset = (int)(WhyDoesJavaNotHaveThese.longValueFromByteArray(arg, false) & 0xFFFF);
	}
	
	public BranchAndKeepIfFalseInstruction(int offset) {
		this.offset = offset;
	}
	
	@Override
	public String displayString() {
		return "BRANCH_AND_KEEP_IF_FALSE (+ 0x" + Integer.toHexString(offset) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x3E, (byte)((offset & 0xFF00) >> 8), (byte)(offset & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x3E;
	}

	@Override
	public int numArgBytes() {
		return 2;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new BranchAndKeepIfFalseInstruction(args);
	}

}
