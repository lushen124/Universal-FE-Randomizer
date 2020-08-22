package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class BitwiseAndInstruction extends ScriptInstruction {
	
	public BitwiseAndInstruction() {
		
	}

	@Override
	public String displayString() {
		return "BITWISE_AND";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x2B};
	}

	@Override
	public byte opcode() {
		return 0x2B;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new BitwiseAndInstruction();
	}

}
