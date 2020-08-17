package fedata.gcnwii.fe9.scripting;

public class AddInstruction extends ScriptInstruction {

	public AddInstruction() {
		
	}
	
	@Override
	public String displayString() {
		return "ADD";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x22};
	}

	@Override
	public byte opcode() {
		return 0x22;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

}
