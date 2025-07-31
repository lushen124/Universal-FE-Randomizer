package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;
import util.WhyDoesJavaNotHaveThese;

public class BranchIfFalseInstruction extends ScriptInstruction {

	int offset;
	
	public BranchIfFalseInstruction(byte[] arg) {
		offset = (int)(WhyDoesJavaNotHaveThese.longValueFromByteArray(arg, false) & 0xFFFF);
	}
	
	public BranchIfFalseInstruction(int offset) {
		this.offset = offset;
	}
	
	@Override
	public String displayString() {
		return "BRANCH_IF_FALSE (+ 0x" + Integer.toHexString(offset) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x3D, (byte)((offset & 0xFF00) >> 8), (byte)(offset & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x3D;
	}

	@Override
	public int numArgBytes() {
		return 2;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new BranchIfFalseInstruction(args);
	}

}
