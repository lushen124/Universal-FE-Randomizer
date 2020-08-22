package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class ReturnInstruction extends ScriptInstruction {

	public ReturnInstruction() {
		
	}
	
	@Override
	public String displayString() {
		return "RETURN";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x39};
	}

	@Override
	public byte opcode() {
		return 0x39;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new ReturnInstruction();
	}

}
