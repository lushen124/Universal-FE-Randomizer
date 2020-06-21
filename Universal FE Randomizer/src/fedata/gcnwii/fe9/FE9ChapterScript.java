package fedata.gcnwii.fe9;

import java.util.ArrayList;
import java.util.List;

import io.gcn.GCNCMBFileHandler;
import util.DebugPrinter;
import util.WhyDoesJavaNotHaveThese;

public class FE9ChapterScript {

	private GCNCMBFileHandler cmbHandler;
	
	private List<Scene> sceneList;
	
	public FE9ChapterScript(GCNCMBFileHandler handler) {
		cmbHandler = handler;
		
		loadScripts();
	}
	
	public void loadScripts() {
		DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "===========================================");
		DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "Loading scripts for " + cmbHandler.getName());
		sceneList = new ArrayList<Scene>();
		
		int scriptTableOffset = (int)cmbHandler.getScriptTableOffset();
		int currentSceneIndex = 0;
		
		byte[] nextHeaderOffset = cmbHandler.cmb_readBytesAtOffset(scriptTableOffset, 4);
		int next = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(nextHeaderOffset, true);
		
		while (next != 0) {
			DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "Loading script " + currentSceneIndex);
			Scene currentScene = new Scene(cmbHandler, scriptTableOffset + (currentSceneIndex * 4));
			sceneList.add(currentScene);
			
			DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tPointer Offset: 0x" + Integer.toHexString(currentScene.pointerOffset).toUpperCase());
			DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tHeader Offset: 0x" + Integer.toHexString(currentScene.sceneHeaderOffset).toUpperCase());
			
			if (currentScene.identifierOffset == 0) {
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tIdentifier Offset: 0x" + Integer.toHexString(currentScene.identifierOffset).toUpperCase());
			} else {
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tIdentifier offset: 0x" + Integer.toHexString(currentScene.identifierOffset).toUpperCase() + " (" + currentScene.identifierName + ")");
			}
			DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tScript Offset: 0x" + Integer.toHexString(currentScene.scriptOffset).toUpperCase());
			DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tParent Offset: 0x" + Integer.toHexString(currentScene.parentOffset).toUpperCase());
			
			DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tScene Kind: 0x" + Integer.toHexString(currentScene.sceneKind).toUpperCase());
			DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tNumber of Arguments: " + currentScene.numberOfArgs);
			DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tNumber of Parameters: " + currentScene.parameterCount);
			
			DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tScene Index: 0x" + Integer.toHexString(currentScene.sceneIndex).toUpperCase());
			DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tVariable Count: " + currentScene.varCount);
			
			for (int i = 0; i < currentScene.params.length; i++) {
				DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\t\tParameter: 0x" + Integer.toHexString(currentScene.params[i]).toUpperCase());
			}
			
			DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tRaw script: " + WhyDoesJavaNotHaveThese.displayStringForBytes(currentScene.scriptBytes));
			
			DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "\tDisassembled:\n" + ScriptInterpreter.disassemble(currentScene, cmbHandler));
			
			currentSceneIndex++;
			nextHeaderOffset = cmbHandler.cmb_readBytesAtOffset(scriptTableOffset + (currentSceneIndex * 4), 4);
			next = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(nextHeaderOffset, true);
		}
		
		DebugPrinter.log(DebugPrinter.Key.FE9_CHAPTER_SCRIPT, "Finished loading scripts for " + cmbHandler.getName());
	}
	
	private class Scene {
		int pointerOffset;
		int sceneHeaderOffset;
		
		int identifierOffset;
		String identifierName;
		int scriptOffset;
		int parentOffset;
		
		byte sceneKind;
		byte numberOfArgs;
		byte parameterCount;
		
		short sceneIndex;
		short varCount;
		
		short[] params;
		
		byte[] scriptBytes;
		
		private Scene(GCNCMBFileHandler handler, int pointerOffset) {
			this.pointerOffset = pointerOffset;
			
			byte[] headerOffset = handler.cmb_readBytesAtOffset(pointerOffset, 4);
			sceneHeaderOffset = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(headerOffset, true);
			
			byte[] identifier = handler.cmb_readBytesAtOffset(sceneHeaderOffset, 4);
			identifierOffset = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(identifier, true);
			if (identifierOffset != 0) {
				byte[] name = handler.cmb_readBytesUpToNextTerminator(identifierOffset);
				identifierName = WhyDoesJavaNotHaveThese.stringFromAsciiBytes(name);
			}
			
			byte[] script = handler.cmb_readBytesAtOffset(sceneHeaderOffset + 4, 4);
			scriptOffset = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(script, true);
			
			byte[] parent = handler.cmb_readBytesAtOffset(sceneHeaderOffset + 8, 4);
			parentOffset = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(parent, true);
			
			sceneKind = handler.cmb_readBytesAtOffset(sceneHeaderOffset + 0xC, 1)[0];
			numberOfArgs = handler.cmb_readBytesAtOffset(sceneHeaderOffset + 0xD, 1)[0];
			parameterCount = handler.cmb_readBytesAtOffset(sceneHeaderOffset + 0xE, 1)[0];
			
			byte[] index = handler.cmb_readBytesAtOffset(sceneHeaderOffset + 0x10, 2);
			sceneIndex = (short)WhyDoesJavaNotHaveThese.longValueFromByteArray(index, true);
			
			byte[] vars = handler.cmb_readBytesAtOffset(sceneHeaderOffset + 0x12, 2);
			varCount = (short)WhyDoesJavaNotHaveThese.longValueFromByteArray(vars, true);
			
			params = new short[parameterCount];
			
			int parsingOffset = sceneHeaderOffset + 0x14;
			int paramIndex = 0;
			while (parsingOffset < scriptOffset && paramIndex < params.length) {
				byte[] parameter = handler.cmb_readBytesAtOffset(parsingOffset, 2);
				params[paramIndex++] = (short)WhyDoesJavaNotHaveThese.longValueFromByteArray(parameter, true);
				parsingOffset += 2;
			}
			
			byte[] nextHeaderOffset = handler.cmb_readBytesAtOffset(pointerOffset + 4, 4);
			int nextHeader = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(nextHeaderOffset, true);
			
			int scriptLength = nextHeader - scriptOffset;
			
			scriptBytes = handler.cmb_readBytesAtOffset(scriptOffset, scriptLength);
		}
	}
	
	private static class ScriptInterpreter {
		private static String disassemble(Scene scene, GCNCMBFileHandler handler) {
			if (scene == null) { return "Null Input"; }
			StringBuilder sb = new StringBuilder();
			
			int index = 0;
			while (index < scene.scriptBytes.length) {
				// Read Opcode
				int opcode = scene.scriptBytes[index++];
				int operand = 0;
				String string = "<null>";
				byte[] buffer;
				
				// Based on opcode, the number of bytes consumed varies. 
				// See https://feuniverse.us/t/fe9-and-fe10-event-script-doc-notes/6143 for more details.
				switch (opcode) {
				case 0x0: // no-op
					sb.append("NOP\n");
					break;
					
				/* These sets of instructions read from local variables and pushes them onto the stack. */
				case 0x1: // pushes value from variable onto stack. Operand is variable number
					operand = scene.scriptBytes[index++];
					sb.append("PUSH_VAL(variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x2: // pushes value from variable onto stack. Operand (2 bytes) is variable number
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]}, false);
					index += 2;
					sb.append("PUSH_VAL2(variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x3: // pushes value from array variable onto stack. Operand is variable number of array. Pops to get array index.
					operand = scene.scriptBytes[index++];
					sb.append("PUSH_ARRAY_ITEM_VAL(array variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x4: // pushes value from array variable onto stack. Operand (2 bytes) is variable number of. Pops to get array index.
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]}, false);
					index += 2;
					sb.append("PUSH_ARRAY_ITEM_VAL2(array variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x5: // pushes value from array variable onto stack. Operand is variable number of pointer to array. Pops to get array index.
					operand = scene.scriptBytes[index++];
					sb.append("PUSH_ARRAY_ITEM_VAL(array pointer variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x6: // pushes value from array variable onto stack. Operand (2 bytes) is variable number of pointer to array. Pops to get array index.
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]}, false);
					index += 2;
					sb.append("PUSH_ARRAY_ITEM_VAL2(array pointer variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x7: // pushes address from variable onto stack. Operand is variable number
					operand = scene.scriptBytes[index++];
					sb.append("PUSH_ADDR(variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x8: // pushes address from variable onto stack. Operand (2 bytes) is variable number
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]}, false);
					index += 2;
					sb.append("PUSH_ADDR2(variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x9: // pushes array item address from array variable onto stack. Operand is variable number of array. Pops to get array index.
					operand = scene.scriptBytes[index++];
					sb.append("PUSH_ARRAY_ITEM_ADDR(array variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0xA: // pushes array item address from array variable onto stack. Operand (2 bytes) is variable number of array. Pops to get array index.
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]}, false);
					index += 2;
					sb.append("PUSH_ARRAY_ITEM_ADDR2(array variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0xB: // pushes array item address from array variable onto stack. Operand is variable number of pointer to array. Pops to get array index.
					operand = scene.scriptBytes[index++];
					sb.append("PUSH_ARRAY_ITEM_ADDR(array pointer variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0xC: // pushes array item address from array variable onto stack. Operand (2 bytes) is variable number of pointer to array. Pops to get array index.
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]}, false);
					index += 2;
					sb.append("PUSH_ARRAY_ITEM_ADDR2(array pointer variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				
				/* These sets of instructions read from global variables and pushes them onto the stack. */
				/* See above for how the operands work. */
				case 0xD: // pushes value from global variable onto stack. 
					operand = scene.scriptBytes[index++];
					sb.append("PUSH_VAL(global variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0xE: // pushes value from global variable onto stack (2 byte operand).
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]}, false);
					index += 2;
					sb.append("PUSH_VAL2(global variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0xF: // pushes value from global array variable onto stack.
					operand = scene.scriptBytes[index++];
					sb.append("PUSH_ARRAY_ITEM_VAL(global array variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x10: // pushes value from global array variable onto stack (2 byte operand).
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]}, false);
					index += 2;
					sb.append("PUSH_ARRAY_ITEM_VAL2(global array variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x11: // pushes value from global array pointer variable onto stack.
					operand = scene.scriptBytes[index++];
					sb.append("PUSH_ARRAY_ITEM_VAL(global array pointer variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x12: // pushes value from global array pointer variable onto stack (2 byte operand).
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]}, false);
					index += 2;
					sb.append("PUSH_ARRAY_ITEM_VAL2(global array pointer variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x13: // pushes address from global variable onto stack.
					operand = scene.scriptBytes[index++];
					sb.append("PUSH_ADDR(global variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x14: // pushes address from global variable onto stack (2 byte operand).
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]}, false);
					index += 2;
					sb.append("PUSH_ADDR2(global variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x15: // pushes array item address from global array variable onto stack.
					operand = scene.scriptBytes[index++];
					sb.append("PUSH_ARRAY_ITEM_ADDR(global array variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x16: // pushes array item address from global array variable onto stack (2 byte operand).
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]}, false);
					index += 2;
					sb.append("PUSH_ARRAY_ITEM_ADDR2(global array variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x17: // pushes array item address from global array pointer variable onto stack.
					operand = scene.scriptBytes[index++];
					sb.append("PUSH_ARRAY_ITEM_ADDR(global array pointer variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x18: // pushes array item address from global array pointer variable onto stack (2 byte operand).
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]}, false);
					index += 2;
					sb.append("PUSH_ARRAY_ITEM_ADDR2(global array pointer variable 0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
					
				/* These instructions push immediate values onto the stack. */
				case 0x19: // pushes operand onto the stack.
					operand = scene.scriptBytes[index++];
					sb.append("PUSH_VALUE(0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x1A: // pushes operand onto the stack (2 byte operand).
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]}, false);
					index += 2;
					sb.append("PUSH_VALUE2(0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x1B: // pushes operand onto the stack (4 byte operand).
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(new byte[] {
							scene.scriptBytes[index], 
							scene.scriptBytes[index+1],
							scene.scriptBytes[index+2],
							scene.scriptBytes[index+3]}, false);
					index += 4;
					sb.append("PUSH_VALUE4(0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x1C: // pushes string with address onto the stack.
					operand = scene.scriptBytes[index++];
					string = handler.stringForOffset(new byte[] {(byte)operand});
					sb.append("PUSH_STRING(offset 0x" + Integer.toHexString(operand).toUpperCase() + " (" + string + "))\n");
					break;
				case 0x1D: // pushes string with address onto the stack (2 byte operand).
					buffer = new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]};
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(buffer, false);
					index += 2;
					string = handler.stringForOffset(buffer);
					sb.append("PUSH_STRING2(offset 0x" + Integer.toHexString(operand).toUpperCase() + " (" + string + "))\n");
					break;
				case 0x1E: // pushes string with address onto the stack (4 byte operand).
					buffer = new byte[] {
							scene.scriptBytes[index], 
							scene.scriptBytes[index+1],
							scene.scriptBytes[index+2],
							scene.scriptBytes[index+3]};
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(buffer, false);
					index += 4;
					string = handler.stringForOffset(buffer);
					sb.append("PUSH_STRING4(offset 0x" + Integer.toHexString(operand).toUpperCase() + " (" + string + "))\n");
					break;
					
				/* These instructions manipulate the items on top of the stack. */
				case 0x1F: // pushes the dereferenced value of the pointer on top of the stack (does not pop pointer).
					sb.append("DEREFERENCE_TOP()\n");
					break;
				case 0x20: // pop and discards the top of the stack.
					sb.append("DISCARD_TOP()\n");
					break;
				case 0x21: // stores value at address. pops the value first, then pops the target address. Also pushes value back onto stack.
					sb.append("STORE_AND_PUSH()\n");
					break;
					
				/* Simple arithmetic operations. The top most value on the stack is always the right operand. The second
				 * to top value is the left operand. */
				case 0x22: // pops the top two items on the stack and pushes their sum.
					sb.append("ADD()\n");
					break;
				case 0x23: // pops and the top two items on the stack and pushes their difference.
					sb.append("REVERSE_SUBTRACT()\n");
					break;
				case 0x24: // pops the top two items on the stack and pushes their product.
					sb.append("MULTIPLY()\n");
					break;
				case 0x25: // pops the top two items on the stack and pushes their quotient.
					sb.append("DIVIDE()\n");
					break;
				case 0x26: // pops the top two items on the stack and pushes the remainder.
					sb.append("MOD()\n");
					break;
				case 0x27: // pops the top value and pushes its negative.
					sb.append("NEGATE()\n");
					break;
					
				/* Bitwise and logical operations. Generally pops the top or two top values and pushes on the result value. */
				case 0x28: // pops the top value and pushes its bitwise negative.
					sb.append("BITWISE_NOT()\n");
					break;
				case 0x29: // pops the top value and pushes 0 if it's not 0 and 1 if it's 0.
					sb.append("LOGICAL_NOT()\n");
					break;
				case 0x2A: // pops two values, pushes bitwise OR
					sb.append("BITWISE_OR()\n");
					break;
				case 0x2B: // pops two values, pushes bitwise AND
					sb.append("BITWISE_AND()\n");
					break;
				case 0x2C: // pops two values, pushes bitwise XOR
					sb.append("BITWISE_XOR()\n");
					break;
				case 0x2D: // pops two values, pushes a left shifted value. The top most is the amount to shift, and the second popped is the value to shift.
					sb.append("LOGICAL_LEFT_SHIFT()\n");
					break;
				case 0x2E: // pops two values, pushes a right shifted value. See above for how the operands work.
					sb.append("LOGICAL_RIGHT_SHIFT()\n");
					break;
					
				/* Comparison operations. As usual, top value is the right side operand. */
				case 0x2F: // pops two values, pushes 1 if both are equal, pushes 0 otherwise.
					sb.append("IS_EQUAL()\n");
					break;
				case 0x30: // pops two values, pushes 1 if not equal, pushes 0 otherwise.
					sb.append("IS_NOT_EQUAL()\n");
					break;
				case 0x31: // pops two values, pushes 1 if second pop is less than first pop, pushes 0 otherwise. (A < B where B is the top value).
					sb.append("LESS_THAN()\n"); // Needs audit.
					break;
				case 0x32: // pops two values, pushes 1 if second pop is less than or equal to first pop. Pushes 0 otherwise.
					sb.append("LESS_THAN_OR_EQUAL()\n");
					break;
				case 0x33: // pops two values, pushes 1 if second pop is greater than first pop, pushes 0 otherwise. (A > B where B is the top value).
					sb.append("GREATER_THAN()\n"); // Needs audit.
					break;
				case 0x34: // pops two values, pushes 1 if second pop is greater than or equal to first pop. PUshes 0 otherwise.
					sb.append("GREATER_THAN()\n"); // Needs audit.
					break;
				case 0x35: // pops two string address values, pushes 1 if strings are equal, pushes 0 otherwise.
					sb.append("STRING_EQUAL()\n");
					break;
				case 0x36: // pops two string address values, pushes 1 if strings are not equal, pushes 0 otherwise.
					sb.append("STRING_NOT_EQUAL()\n");
					break;
					
				/* Control codes */
				case 0x37: // Calls scene from the same script (operand is ID)
					operand = scene.scriptBytes[index++]; // Note: This works slightly different in FE10.
					sb.append("CALL_SCENE_ID(0x" + Integer.toHexString(operand).toUpperCase() + ")\n");
					break;
				case 0x38: // Calls scene/function by name
					buffer = new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]};
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(buffer, false);
					index += 2;
					string = handler.stringForOffset(buffer);
					sb.append("CALL_SCENE_NAME(" + string + ", " + scene.scriptBytes[index++] + " args)\n");
					break;
				case 0x39: // Return from function.
					sb.append("RETURN()\n");
					break;
				case 0x3A: // Unconditional Branch
					buffer = new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]};
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(buffer, false);
					index += 2;
					sb.append("BRANCH(PC + 1 + " + operand + ")\n");
					break;
				case 0x3B: // Pops top value, branch if not equal to 0.
					buffer = new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]};
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(buffer, false);
					index += 2;
					sb.append("POP_AND_BRANCH_IF_TRUE(PC + 1 + " + operand + ")\n");
					break;
				case 0x3C: // Pops top value, pushes 1 and branch if not equal to 0.
					buffer = new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]};
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(buffer, false);
					index += 2;
					sb.append("POP_AND_PUSH_BRANCH_IF_TRUE(PC + 1 + " + operand + ")\n");
					break;
				case 0x3D: // Pops top value, branch if equal to 0.
					buffer = new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]};
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(buffer, false);
					index += 2;
					sb.append("POP_AND_BRANCH_IF_FALSE(PC + 1 + " + operand + ")\n");
					break;
				case 0x3E: // Pops top value, pushes 1 and branch if equal to 0.
					buffer = new byte[] {scene.scriptBytes[index], scene.scriptBytes[index+1]};
					operand = (int)WhyDoesJavaNotHaveThese.longValueFromByteArray(buffer, false);
					index += 2;
					sb.append("POP_AND_PUSH_BRANCH_IF_FALSE(PC + 1 + " + operand + ")\n");
					break;
				case 0x3F: // Holds scene without ending.
					sb.append("YIELD");
					break;
				case 0x40: // ?
					sb.append("INSTRUCTION 0x40");
					break;
				case 0x41: // Dummied printf
					sb.append("POP_AND_PRINT(" + scene.scriptBytes[index++] + " values)\n");
					break;
				
				// 0x42 - 0x47 are FE10 only.
				default:
					sb.append("UNKNOWN INSTRUCTION (0x" + Integer.toHexString(opcode) + ")\n");
					break;
				}
				
				// If we see a RETURN(), that's the end of the function.
				if (opcode == 0x39) {
					break;
				}
			}
			
			return sb.toString();
		}
	}
}
