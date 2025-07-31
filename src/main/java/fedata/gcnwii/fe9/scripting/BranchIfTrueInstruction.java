package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;
import util.WhyDoesJavaNotHaveThese;

public class BranchIfTrueInstruction extends ScriptInstruction {

	int offset;
	
	public BranchIfTrueInstruction(byte[] arg) {
		offset = (int)(WhyDoesJavaNotHaveThese.longValueFromByteArray(arg, false) & 0xFFFF);
	}
	
	public BranchIfTrueInstruction(int offset) { // branches to specified offset + 1
		this.offset = offset;
	}
	
	@Override
	public String displayString() {
		return "BRANCH_IF_TRUE (+ 0x" + Integer.toHexString(offset) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x3B, (byte)((offset & 0xFF00) >> 8), (byte)(offset & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x3B;
	}

	@Override
	public int numArgBytes() {
		return 2;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new BranchIfTrueInstruction(args);
	}

}
