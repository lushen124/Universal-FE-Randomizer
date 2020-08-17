package fedata.gcnwii.fe9.scripting;

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

}
