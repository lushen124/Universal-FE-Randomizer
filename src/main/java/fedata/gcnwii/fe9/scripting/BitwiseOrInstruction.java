package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class BitwiseOrInstruction extends ScriptInstruction {

	public BitwiseOrInstruction() {
		
	}
	
	@Override
	public String displayString() {
		return "BITWISE_OR";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x2A};
	}

	@Override
	public byte opcode() {
		return 0x2A;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new BitwiseOrInstruction();
	}
}
