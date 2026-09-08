package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class DivideInstruction extends ScriptInstruction {

	public DivideInstruction() {
		
	}
	
	@Override
	public String displayString() {
		return "DIVIDE";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x25};
	}

	@Override
	public byte opcode() {
		return 0x25;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new DivideInstruction();
	}

}
