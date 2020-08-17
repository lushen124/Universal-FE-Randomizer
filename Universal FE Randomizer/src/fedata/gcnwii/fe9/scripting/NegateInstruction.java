package fedata.gcnwii.fe9.scripting;

public class NegateInstruction extends ScriptInstruction {

	public NegateInstruction() {
		
	}
	
	@Override
	public String displayString() {
		return "NEGATE";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x28};
	}

	@Override
	public byte opcode() {
		return 0x28;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

}
