package fedata.gcnwii.fe9.scripting;

public abstract class ScriptInstruction {
	
	public abstract String displayString();
	public abstract byte[] rawBytes();
	
	public abstract byte opcode();
	public abstract int numArgBytes();
}
