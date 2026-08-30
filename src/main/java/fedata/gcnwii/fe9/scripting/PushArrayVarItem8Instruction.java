package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class PushArrayVarItem8Instruction extends ScriptInstruction {
	
	int arrayVariable;
	
	public PushArrayVarItem8Instruction(byte[] arg) {
		arrayVariable = arg[0];
	}
	
	public PushArrayVarItem8Instruction(int arrayVariable) {
		this.arrayVariable = arrayVariable & 0xFF;
	}

	@Override
	public String displayString() {
		return "PUSH_ARRAY_VAR_ITEM_8 (0x" + Integer.toHexString(arrayVariable) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x3, (byte)(arrayVariable & 0xFF) };
	}

	@Override
	public byte opcode() {
		return 0x3;
	}

	@Override
	public int numArgBytes() {
		return 1;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new PushArrayVarItem8Instruction(args);
	}

}
