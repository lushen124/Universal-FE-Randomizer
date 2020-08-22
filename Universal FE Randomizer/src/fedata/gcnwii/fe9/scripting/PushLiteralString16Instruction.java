package fedata.gcnwii.fe9.scripting;

import java.util.Arrays;

import io.gcn.GCNCMBFileHandler;

public class PushLiteralString16Instruction extends ScriptInstruction {

	String literal;
	GCNCMBFileHandler handler;
	
	public PushLiteralString16Instruction(byte[] arg, GCNCMBFileHandler handler) {
		literal = handler.stringForOffset(Arrays.copyOf(arg, 2));
		this.handler = handler;
	}
	
	public PushLiteralString16Instruction(String literal, GCNCMBFileHandler handler) {
		this.literal = literal;
		this.handler = handler;
	}
	
	@Override
	public String displayString() {
		return "PUSH_LITERAL_STRING_16 (" + literal + ")";
	}

	@Override
	public byte[] rawBytes() {
		byte[] referenceToString = handler.referenceToString(literal);
		if (referenceToString == null) { 
			handler.addString(literal);
			referenceToString = handler.referenceToString(literal);
		}
		assert(referenceToString != null);
		assert(referenceToString.length == numArgBytes());
		return new byte[] {0x1D, referenceToString[0], referenceToString[1]};
	}

	@Override
	public byte opcode() {
		// TODO Auto-generated method stub
		return 0x1D;
	}

	@Override
	public int numArgBytes() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new PushLiteralString16Instruction(args, handler);
	}

}
