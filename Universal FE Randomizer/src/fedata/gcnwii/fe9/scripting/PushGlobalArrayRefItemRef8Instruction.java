package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class PushGlobalArrayRefItemRef8Instruction extends ScriptInstruction {

	int globalPointerVariable;
	
	public PushGlobalArrayRefItemRef8Instruction(byte[] arg) {
		globalPointerVariable = arg[0] & 0xFF;
	}
	
	public PushGlobalArrayRefItemRef8Instruction(int globalPointerVariable) {
		this.globalPointerVariable = globalPointerVariable & 0xFF;
	}
	
	@Override
	public String displayString() {
		return "PUSH_GLOBAL_ARRAY_REF_ITEM_REF_8 (0x" + Integer.toHexString(globalPointerVariable) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x17, (byte)(globalPointerVariable & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x17;
	}

	@Override
	public int numArgBytes() {
		return 1;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new PushGlobalArrayRefItemRef8Instruction(args);
	}

}
