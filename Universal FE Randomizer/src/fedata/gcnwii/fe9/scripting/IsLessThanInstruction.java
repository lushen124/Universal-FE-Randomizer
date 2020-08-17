package fedata.gcnwii.fe9.scripting;

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

}
