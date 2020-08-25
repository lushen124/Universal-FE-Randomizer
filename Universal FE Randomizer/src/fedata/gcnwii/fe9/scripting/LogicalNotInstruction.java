package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class LogicalNotInstruction extends ScriptInstruction {

	public LogicalNotInstruction() {
		
	}
	
	@Override
	public String displayString() {
		return "LOGICAL_NOT";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x29};
	}

	@Override
	public byte opcode() {
		return 0x29;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new LogicalNotInstruction();
	}
	
	

}
