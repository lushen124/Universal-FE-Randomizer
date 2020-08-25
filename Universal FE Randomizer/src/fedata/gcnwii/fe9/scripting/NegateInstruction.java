package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class NegateInstruction extends ScriptInstruction {

	public NegateInstruction() {
		
	}
	
	@Override
	public String displayString() {
		return "NEGATE";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x28};
	}

	@Override
	public byte opcode() {
		return 0x28;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new NegateInstruction();
	}

}
