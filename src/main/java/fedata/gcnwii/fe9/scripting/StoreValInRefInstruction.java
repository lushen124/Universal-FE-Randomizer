package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class StoreValInRefInstruction extends ScriptInstruction {
	
	public StoreValInRefInstruction() {
		
	}

	@Override
	public String displayString() {
		return "STORE_VAL_IN_REF";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x21};
	}

	@Override
	public byte opcode() {
		return 0x21;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new StoreValInRefInstruction();
	}

}
