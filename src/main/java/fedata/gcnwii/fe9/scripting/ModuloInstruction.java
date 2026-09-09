package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class ModuloInstruction extends ScriptInstruction {

	public ModuloInstruction() {
		
	}
	
	@Override
	public String displayString() {
		return "MODULO";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x26};
	}

	@Override
	public byte opcode() {
		return 0x26;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new ModuloInstruction();
	}

}
