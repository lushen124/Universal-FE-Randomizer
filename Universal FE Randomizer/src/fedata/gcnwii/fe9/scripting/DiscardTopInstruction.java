package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class DiscardTopInstruction extends ScriptInstruction {

	public DiscardTopInstruction() {
		
	}
	
	@Override
	public String displayString() {
		return "DISCARD_TOP";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x20};
	}

	@Override
	public byte opcode() {
		return 0x20;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new DiscardTopInstruction();
	}

}
