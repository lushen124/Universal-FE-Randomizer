package fedata.gcnwii.fe9.scripting;

import java.util.Arrays;

import io.gcn.GCNCMBFileHandler;

public class PushLiteralString32Instruction extends ScriptInstruction {

	String literal;
	GCNCMBFileHandler handler;
	
	public PushLiteralString32Instruction(byte[] arg, GCNCMBFileHandler handler) {
		literal = handler.stringForOffset(Arrays.copyOf(arg, 4));
		this.handler = handler;
	}
	
	public PushLiteralString32Instruction(String literal, GCNCMBFileHandler handler) {
		this.literal = literal;
		this.handler = handler;
	}
	
	@Override
	public String displayString() {
		return "PUSH_LITERAL_STRING_32 (" + literal + ")";
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
		return new byte[] {0x1E, referenceToString[0], referenceToString[1], referenceToString[2], referenceToString[3]};
	}

	@Override
	public byte opcode() {
		// TODO Auto-generated method stub
		return 0x1E;
	}

	@Override
	public int numArgBytes() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new PushLiteralString32Instruction(args, handler);
	}

}
