package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class PrintFInstruction extends ScriptInstruction {

	byte arg;
	
	public PrintFInstruction(byte[] arg) {
		this.arg = arg[0];
	}
	
	public PrintFInstruction(byte arg) {
		this.arg = arg;
	}
	
	@Override
	public String displayString() {
		return "PRINTF(0x" + Integer.toHexString(arg) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x41, arg};
	}

	@Override
	public byte opcode() {
		return 0x41;
	}

	@Override
	public int numArgBytes() {
		return 1;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new PrintFInstruction(args);
	}

}
