package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class IsLessThanInstruction extends ScriptInstruction {

	public IsLessThanInstruction() {
		
	}
	
	@Override
	public String displayString() {
		return "IS_LESS_THAN";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x31};
	}

	@Override
	public byte opcode() {
		return 0x31;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}


	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new IsLessThanInstruction();
	}

}
