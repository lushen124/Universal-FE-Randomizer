package fedata.gcnwii.fe9.scripting;

public class SubtractInstruction extends ScriptInstruction {

	public SubtractInstruction() {
		
	}
	
	@Override
	public String displayString() {
		return "SUBTRACT";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x23};
	}

	@Override
	public byte opcode() {
		return 0x23;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

}
