package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class YieldInstruction extends ScriptInstruction {

	public YieldInstruction() {
		
	}
	
	@Override
	public String displayString() {
		return "YIELD";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x3F};
	}

	@Override
	public byte opcode() {
		return 0x3F;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new YieldInstruction();
	}

}
