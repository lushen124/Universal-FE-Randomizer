package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class PushGlobalArrayRefItem8Instruction extends ScriptInstruction {

	int globalPointerVariable;
	
	public PushGlobalArrayRefItem8Instruction(byte[] arg) {
		globalPointerVariable = arg[0] & 0xFF;
	}
	
	public PushGlobalArrayRefItem8Instruction(int globalPointerVariable) {
		this.globalPointerVariable = globalPointerVariable & 0xFF;
	}
	
	@Override
	public String displayString() {
		return "PUSH_GLOBAL_ARRAY_REF_ITEM_8 (0x" + Integer.toHexString(globalPointerVariable) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x11, (byte)(globalPointerVariable & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x11;
	}

	@Override
	public int numArgBytes() {
		return 1;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new PushGlobalArrayRefItem8Instruction(args);
	}

}
