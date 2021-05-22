package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;
import util.ByteArrayBuilder;

public class ReturnInstruction extends ScriptInstruction {
	
	private byte[] remainder;

	public ReturnInstruction() {
		
	}
	
	@Override
	public String displayString() {
		return "RETURN";
	}

	@Override
	public byte[] rawBytes() {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendByte((byte)0x39);
		builder.appendBytes(remainder);
		return builder.toByteArray();
	}

	@Override
	public byte opcode() {
		return 0x39;
	}

	@Override
	public int numArgBytes() {
		return 0;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new ReturnInstruction();
	}
	
	public void setRemainder(byte[] remainder) {
		this.remainder = remainder;
	}
	
	public byte[] getRemainder() {
		return remainder;
	}

}
