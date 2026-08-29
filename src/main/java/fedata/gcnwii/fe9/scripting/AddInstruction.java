package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class AddInstruction extends ScriptInstruction {

	public AddInstruction() {
		
	}
	
	@Override
	public String displayString() {
		return "ADD";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x22};
	}

	@Override
	public byte opcode() {
		return 0x22;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new AddInstruction();
	}

}
