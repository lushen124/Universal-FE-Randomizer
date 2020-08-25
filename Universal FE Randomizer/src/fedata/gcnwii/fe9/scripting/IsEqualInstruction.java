package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class IsEqualInstruction extends ScriptInstruction {

	public IsEqualInstruction() {
		
	}

	@Override
	public String displayString() {
		return "IS_EQUAL";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x2F};
	}

	@Override
	public byte opcode() {
		return 0x2F;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new IsEqualInstruction();
	}
}
