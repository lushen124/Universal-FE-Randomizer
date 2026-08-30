package fedata.gcnwii.fe9.scripting;

import java.util.Arrays;

import io.gcn.GCNCMBFileHandler;

public class CallSceneByNameInstruction extends ScriptInstruction {

	String sceneName;
	int numberOfArgs;
	
	GCNCMBFileHandler handler;
	
	public CallSceneByNameInstruction(byte[] args, GCNCMBFileHandler handler) {
		sceneName = handler.stringForOffset(Arrays.copyOf(args, 2));
		numberOfArgs = args[2];
		this.handler = handler;
	}
	
	public CallSceneByNameInstruction(String sceneName, int numberOfArgs, GCNCMBFileHandler handler) {
		this.sceneName = sceneName;
		this.numberOfArgs = numberOfArgs;
		this.handler = handler;
	}
	
	public String getSceneName() {
		return sceneName;
	}
	
	@Override
	public String displayString() {
		return "CALL_SCENE_NAME (\"" + sceneName + "\", " + numberOfArgs + ")";
	}

	@Override
	public byte[] rawBytes() {
		byte[] referenceToString = handler.referenceToString(sceneName, 2);
		if (referenceToString == null) { 
			handler.addString(sceneName);
			referenceToString = handler.referenceToString(sceneName, 2);
		}
		assert(referenceToString != null);
		assert(referenceToString.length == 2);
		return new byte[] {0x38, referenceToString[0], referenceToString[1], (byte)(numberOfArgs & 0xFF)};
	}

	@Override
	public byte opcode() {
		return 0x38;
	}

	@Override
	public int numArgBytes() {
		return 3;
	}

	@Override
	public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
		return new CallSceneByNameInstruction(args, handler);
	}

}
