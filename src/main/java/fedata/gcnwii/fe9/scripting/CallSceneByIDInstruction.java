package fedata.gcnwii.fe9.scripting;

import io.gcn.GCNCMBFileHandler;

public class CallSceneByIDInstruction extends ScriptInstruction {

	int sceneID;
	
	public CallSceneByIDInstruction(byte[] arg) {
		sceneID = arg[0] & 0xFF;
	}
	
	public CallSceneByIDInstruction(int sceneID) {
		this.sceneID = sceneID;
	}
	
	@Override
	public String displayString() {
		return "CALL_SCENE_ID (0x" + Integer.toHexString(sceneID) + ")";
	}

	@Override
	public byte[] rawBytes() {
		return new byte[] {0x37, (byte)(sceneID & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x37;
	}

	@Override
	public int numArgBytes() {
		return 1; // For FE9 at least.
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new CallSceneByIDInstruction(args);
	}

}
