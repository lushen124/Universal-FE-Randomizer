package fedata.gcnwii.fe9.scripting;

public class MultiplyInstruction extends ScriptInstruction {

	public MultiplyInstruction() {
		
	}
	
	@Override
	public String displayString() {
		return "MULTIPLY";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x24};
	}

	@Override
	public byte opcode() {
		return 0x24;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

}
