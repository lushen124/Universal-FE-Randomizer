package fedata.gcnwii.fe9.scripting;

public class IsStringEqualInstruction extends ScriptInstruction{

	public IsStringEqualInstruction() {
		
	}
	
	@Override
	public String displayString() {
		return "IS_STRING_EQUAL";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x35};
	}

	@Override
	public byte opcode() {
		return 0x35;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

}
