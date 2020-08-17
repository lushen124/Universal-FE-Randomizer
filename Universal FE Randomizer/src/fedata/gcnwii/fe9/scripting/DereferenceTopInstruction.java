package fedata.gcnwii.fe9.scripting;

public class DereferenceTopInstruction extends ScriptInstruction {

	public DereferenceTopInstruction() {
		
	}
	
	@Override
	public String displayString() {
		return "DEREFERENCE_TOP";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x1F};
	}

	@Override
	public byte opcode() {
		return 0x1F;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

}
