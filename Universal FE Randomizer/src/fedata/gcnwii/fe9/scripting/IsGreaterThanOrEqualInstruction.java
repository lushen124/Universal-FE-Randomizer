package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class IsGreaterThanOrEqualInstruction extends ScriptInstruction {

	public IsGreaterThanOrEqualInstruction() {
		
	}
	
	@Override
	public String displayString() {
		return "IS_GREATER_THAN_OR_EQUAL";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x34};
	}

	@Override
	public byte opcode() {
		return 0x34;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new IsGreaterThanOrEqualInstruction();
	}

}
