package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public abstract class ScriptInstruction {
	
	public abstract String displayString();
	public abstract byte[] rawBytes();
	
	public abstract byte opcode();
	public abstract int numArgBytes();
	
	public abstract ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler);
}
