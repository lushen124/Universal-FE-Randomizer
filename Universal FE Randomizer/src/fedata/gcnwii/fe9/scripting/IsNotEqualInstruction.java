package fedata.gcnwii.fe9.scripting;

public class IsNotEqualInstruction extends ScriptInstruction {

	public IsNotEqualInstruction() {
		
	}

	@Override
	public String displayString() {
		return "IS_NOT_EQUAL";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x30};
	}

	@Override
	public byte opcode() {
		return 0x30;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}
}
