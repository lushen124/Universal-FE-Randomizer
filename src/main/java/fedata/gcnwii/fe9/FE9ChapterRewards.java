package fedata.gcnwii.fe9;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fedata.gcnwii.fe9.scripting.CallSceneByNameInstruction;
import fedata.gcnwii.fe9.scripting.PushLiteralString16Instruction;
import fedata.gcnwii.fe9.scripting.PushLiteralString32Instruction;
import fedata.gcnwii.fe9.scripting.PushLiteralString8Instruction;
import fedata.gcnwii.fe9.scripting.ScriptInstruction;
import io.gcn.GCNCMBFileHandler;
import util.DebugPrinter;

public class FE9ChapterRewards {
	
	private GCNCMBFileHandler cmbHandler;
	
	private Map<FE9ScriptScene, List<Integer>> scenesWithItemsByInstructionIndices;
	
	public FE9ChapterRewards(GCNCMBFileHandler handler) {
		cmbHandler = handler;
		
		scenesWithItemsByInstructionIndices = new HashMap<FE9ScriptScene, List<Integer>>();
		
		loadRewards();
	}
	
	public void replaceRewards(FE9ChapterRewardsProcessor processor) {
		for (FE9ScriptScene script : scenesWithItemsByInstructionIndices.keySet()) {
			List<Integer> instructionIndex = scenesWithItemsByInstructionIndices.get(script);
			List<ScriptInstruction> instructions = script.getInstructions();
			for (Integer index : instructionIndex) {
				ScriptInstruction instruction = instructions.get(index);
				String oldIID = null;
				boolean use32 = false;
				if (instruction instanceof PushLiteralString8Instruction) {
					PushLiteralString8Instruction string8Instruction = (PushLiteralString8Instruction)instruction;
					oldIID = string8Instruction.getString();
				} else if (instruction instanceof PushLiteralString16Instruction) {
					PushLiteralString16Instruction string16Instruction = (PushLiteralString16Instruction)instruction;
					oldIID = string16Instruction.getString();
				} else if (instruction instanceof PushLiteralString32Instruction) {
					PushLiteralString32Instruction string32Instruction = (PushLiteralString32Instruction)instruction;
					oldIID = string32Instruction.getString();
					use32 = true;
				}
				if (oldIID != null) {
					String newIID = processor.replaceItem(oldIID);
					if (newIID == null) {
						DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "No replacement returned for " + oldIID + ". Skipping...");
						continue;
					}
					instructions.remove(index.intValue());
					if (use32) {
						instructions.add(index, new PushLiteralString32Instruction(newIID, cmbHandler));
					} else {
						instructions.add(index, new PushLiteralString16Instruction(newIID, cmbHandler));
					}
				}
			}
			script.setInstructions(instructions);
		}
	}
	
	public void commitChanges() {
//		for (FE9ScriptScene scene : cmbHandler.getScenes()) {
//			scene.commit();
//		}
	}
	
	private void loadRewards() {
		for (FE9ScriptScene scene : cmbHandler.getScenes()) {
			List<ScriptInstruction> instructions = scene.getInstructions();
			for (int i = 1; i < instructions.size(); i++) {
				ScriptInstruction instruction = instructions.get(i);
				if (instruction instanceof CallSceneByNameInstruction) {
					CallSceneByNameInstruction csbn = (CallSceneByNameInstruction)instruction;
					if (csbn.getSceneName().equals("MindGetItem")) {
						ScriptInstruction previousInstruction = instructions.get(i - 1);
						if (previousInstruction instanceof PushLiteralString8Instruction) {
							PushLiteralString8Instruction itemInstruction = (PushLiteralString8Instruction)previousInstruction;
							if (itemInstruction.getString().startsWith("IID_")) {
								List<Integer> indices = scenesWithItemsByInstructionIndices.get(scene);
								if (indices == null) {
									indices = new ArrayList<Integer>();
									scenesWithItemsByInstructionIndices.put(scene, indices);
								}
								indices.add(i - 1);
							}
						} else if (previousInstruction instanceof PushLiteralString16Instruction) {
							PushLiteralString16Instruction itemInstruction = (PushLiteralString16Instruction)previousInstruction;
							if (itemInstruction.getString().startsWith("IID_")) {
								List<Integer> indices = scenesWithItemsByInstructionIndices.get(scene);
								if (indices == null) {
									indices = new ArrayList<Integer>();
									scenesWithItemsByInstructionIndices.put(scene, indices);
								}
								indices.add(i - 1);
							}
						} else if (previousInstruction instanceof PushLiteralString32Instruction) {
							PushLiteralString32Instruction itemInstruction = (PushLiteralString32Instruction)previousInstruction;
							if (itemInstruction.getString().startsWith("IID_")) {
								List<Integer> indices = scenesWithItemsByInstructionIndices.get(scene);
								if (indices == null) {
									indices = new ArrayList<Integer>();
									scenesWithItemsByInstructionIndices.put(scene, indices);
								}
								indices.add(i - 1);
							}
						}
					}
				}
			}
		}
	}
}
