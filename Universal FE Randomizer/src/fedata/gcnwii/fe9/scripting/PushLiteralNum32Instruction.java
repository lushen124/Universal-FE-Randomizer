package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;
import util.WhyDoesJavaNotHaveThese;

public class PushLiteralNum32Instruction extends ScriptInstruction {

	int literal;
	
	public PushLiteralNum32Instruction(byte[] arg) {
		literal = (int)(WhyDoesJavaNotHaveThese.longValueFromByteArray(arg, false) & 0xFFFFFFFF);
	}
	
	public PushLiteralNum32Instruction(int literal) {
		this.literal = literal & 0xFFFFFFFF;
	}
	
	@Override
	public String displayString() {
		return "PUSH_LITERAL_NUM_32 (0x" + Integer.toHexString(literal) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {
				0x1B,
				(byte)((literal & 0xFF000000) >> 24),
				(byte)((literal & 0x00FF0000) >> 16),
				(byte)((literal & 0x0000FF00) >> 8),
				(byte)(literal & 0x000000FF)
		};
	}

	@Override
	public byte opcode() {
		return 0x1B;
	}

	@Override
	public int numArgBytes() {
		return 4;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new PushLiteralNum32Instruction(args);
	}

}
