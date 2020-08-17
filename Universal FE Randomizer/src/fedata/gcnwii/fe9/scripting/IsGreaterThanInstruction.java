package fedata.gcnwii.fe9.scripting;

public class IsGreaterThanInstruction extends ScriptInstruction {

	public IsGreaterThanInstruction() {
		
	}
	
	@Override
	public String displayString() {
		return "IS_GREATER_THAN";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x33};
	}

	@Override
	public byte opcode() {
		return 0x33;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

}
