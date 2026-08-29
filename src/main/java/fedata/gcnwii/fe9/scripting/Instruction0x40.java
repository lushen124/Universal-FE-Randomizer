package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class Instruction0x40 extends ScriptInstruction {
	
	byte[] args;

	public Instruction0x40(byte[] args) {
		this.args = args;
	}
	
	@Override
	public String displayString() {
		return "INSTRUCTION_0x40";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x40, args[0], args[1], args[2], args[3]};
	}

	@Override
	public byte opcode() {
		return 0x40;
	}

	@Override
	public int numArgBytes() {
		return 4;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new Instruction0x40(args);
	}

}
