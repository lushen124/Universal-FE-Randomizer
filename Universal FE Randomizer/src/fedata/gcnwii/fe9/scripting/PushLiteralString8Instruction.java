package fedata.gcnwii.fe9.scripting;

import java.util.Arrays;

import io.gcn.GCNCMBFileHandler;

public class PushLiteralString8Instruction extends ScriptInstruction {

	String literal;
	GCNCMBFileHandler handler;
	
	public PushLiteralString8Instruction(byte[] arg, GCNCMBFileHandler handler) {
		literal = handler.stringForOffset(Arrays.copyOf(arg, 1));
		this.handler = handler;
	}
	
	public PushLiteralString8Instruction(String literal, GCNCMBFileHandler handler) {
		this.literal = literal;
		this.handler = handler;
	}
	
	public String getString() {
		return literal;
	}
	
	@Override
	public String displayString() {
		return "PUSH_LITERAL_STRING_8 (" + literal + ")";
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
		return new byte[] {0x1C, referenceToString[0]};
	}

	@Override
	public byte opcode() {
		return 0x1C;
	}

	@Override
	public int numArgBytes() {
		return 1;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new PushLiteralString8Instruction(args, handler);
	}

}
