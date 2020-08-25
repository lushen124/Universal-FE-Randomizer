package fedata.gcnwii.fe9;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fedata.gcnwii.fe9.scripting.AddInstruction;
import fedata.gcnwii.fe9.scripting.BitwiseAndInstruction;
import fedata.gcnwii.fe9.scripting.BitwiseLeftShiftInstruction;
import fedata.gcnwii.fe9.scripting.BitwiseNegateInstruction;
import fedata.gcnwii.fe9.scripting.BitwiseOrInstruction;
import fedata.gcnwii.fe9.scripting.BitwiseRightShiftInstruction;
import fedata.gcnwii.fe9.scripting.BitwiseXorInstruction;
import fedata.gcnwii.fe9.scripting.BranchAndKeepIfFalseInstruction;
import fedata.gcnwii.fe9.scripting.BranchAndKeepIfTrueInstruction;
import fedata.gcnwii.fe9.scripting.BranchIfFalseInstruction;
import fedata.gcnwii.fe9.scripting.BranchIfTrueInstruction;
import fedata.gcnwii.fe9.scripting.BranchInstruction;
import fedata.gcnwii.fe9.scripting.CallSceneByIDInstruction;
import fedata.gcnwii.fe9.scripting.CallSceneByNameInstruction;
import fedata.gcnwii.fe9.scripting.DereferenceTopInstruction;
import fedata.gcnwii.fe9.scripting.DiscardTopInstruction;
import fedata.gcnwii.fe9.scripting.DivideInstruction;
import fedata.gcnwii.fe9.scripting.Instruction0x40;
import fedata.gcnwii.fe9.scripting.IsEqualInstruction;
import fedata.gcnwii.fe9.scripting.IsGreaterThanInstruction;
import fedata.gcnwii.fe9.scripting.IsGreaterThanOrEqualInstruction;
import fedata.gcnwii.fe9.scripting.IsLessThanInstruction;
import fedata.gcnwii.fe9.scripting.IsLessThanOrEqualInstruction;
import fedata.gcnwii.fe9.scripting.IsNotEqualInstruction;
import fedata.gcnwii.fe9.scripting.IsStringEqualInstruction;
import fedata.gcnwii.fe9.scripting.IsStringNotEqualInstruction;
import fedata.gcnwii.fe9.scripting.LogicalNotInstruction;
import fedata.gcnwii.fe9.scripting.ModuloInstruction;
import fedata.gcnwii.fe9.scripting.MultiplyInstruction;
import fedata.gcnwii.fe9.scripting.NOPInstruction;
import fedata.gcnwii.fe9.scripting.NegateInstruction;
import fedata.gcnwii.fe9.scripting.PrintFInstruction;
import fedata.gcnwii.fe9.scripting.PushArrayRefItem16Instruction;
import fedata.gcnwii.fe9.scripting.PushArrayRefItem8Instruction;
import fedata.gcnwii.fe9.scripting.PushArrayRefItemRef16Instruction;
import fedata.gcnwii.fe9.scripting.PushArrayRefItemRef8Instruction;
import fedata.gcnwii.fe9.scripting.PushArrayVarItem16Instruction;
import fedata.gcnwii.fe9.scripting.PushArrayVarItem8Instruction;
import fedata.gcnwii.fe9.scripting.PushArrayVarItemRef16Instruction;
import fedata.gcnwii.fe9.scripting.PushArrayVarItemRef8Instruction;
import fedata.gcnwii.fe9.scripting.PushGlobalArrayRefItem16Instruction;
import fedata.gcnwii.fe9.scripting.PushGlobalArrayRefItem8Instruction;
import fedata.gcnwii.fe9.scripting.PushGlobalArrayRefItemRef16Instruction;
import fedata.gcnwii.fe9.scripting.PushGlobalArrayRefItemRef8Instruction;
import fedata.gcnwii.fe9.scripting.PushGlobalArrayVarItem16Instruction;
import fedata.gcnwii.fe9.scripting.PushGlobalArrayVarItem8Instruction;
import fedata.gcnwii.fe9.scripting.PushGlobalArrayVarItemRef16Instruction;
import fedata.gcnwii.fe9.scripting.PushGlobalArrayVarItemRef8Instruction;
import fedata.gcnwii.fe9.scripting.PushGlobalVar16Instruction;
import fedata.gcnwii.fe9.scripting.PushGlobalVar8Instruction;
import fedata.gcnwii.fe9.scripting.PushGlobalVarRef16Instruction;
import fedata.gcnwii.fe9.scripting.PushGlobalVarRef8Instruction;
import fedata.gcnwii.fe9.scripting.PushLiteralNum16Instruction;
import fedata.gcnwii.fe9.scripting.PushLiteralNum32Instruction;
import fedata.gcnwii.fe9.scripting.PushLiteralNum8Instruction;
import fedata.gcnwii.fe9.scripting.PushLiteralString16Instruction;
import fedata.gcnwii.fe9.scripting.PushLiteralString32Instruction;
import fedata.gcnwii.fe9.scripting.PushLiteralString8Instruction;
import fedata.gcnwii.fe9.scripting.PushVar16Instruction;
import fedata.gcnwii.fe9.scripting.PushVar8Instruction;
import fedata.gcnwii.fe9.scripting.PushVarRef16Instruction;
import fedata.gcnwii.fe9.scripting.PushVarRef8Instruction;
import fedata.gcnwii.fe9.scripting.ReturnInstruction;
import fedata.gcnwii.fe9.scripting.ScriptInstruction;
import fedata.gcnwii.fe9.scripting.StoreValInRefInstruction;
import fedata.gcnwii.fe9.scripting.SubtractInstruction;
import fedata.gcnwii.fe9.scripting.YieldInstruction;
import io.gcn.GCNCMBFileHandler;

public class FE9ScriptInterpreter {
	
	static Map<Byte, ScriptInstruction> instructionMap;
	
	static {
		instructionMap = new HashMap<Byte, ScriptInstruction>();
		
		// The actual data doesn't actually matter since these aren't real instructions.
		byte[] dummyData = new byte[4];
		
		AddInstruction add = new AddInstruction();
		instructionMap.put(add.opcode(), add);
		BitwiseAndInstruction bitwiseAnd = new BitwiseAndInstruction();
		instructionMap.put(bitwiseAnd.opcode(), bitwiseAnd);
		BitwiseLeftShiftInstruction leftShift = new BitwiseLeftShiftInstruction();
		instructionMap.put(leftShift.opcode(), leftShift);
		BitwiseNegateInstruction bitwiseNegate = new BitwiseNegateInstruction();
		instructionMap.put(bitwiseNegate.opcode(), bitwiseNegate);
		BitwiseOrInstruction bitwiseOr = new BitwiseOrInstruction();
		instructionMap.put(bitwiseOr.opcode(), bitwiseOr);
		BitwiseRightShiftInstruction rightShift = new BitwiseRightShiftInstruction();
		instructionMap.put(rightShift.opcode(), rightShift);
		BitwiseXorInstruction bitwiseXor = new BitwiseXorInstruction();
		instructionMap.put(bitwiseXor.opcode(), bitwiseXor);
		BranchAndKeepIfFalseInstruction bkf = new BranchAndKeepIfFalseInstruction(dummyData);
		instructionMap.put(bkf.opcode(), bkf);
		BranchAndKeepIfTrueInstruction bkt = new BranchAndKeepIfTrueInstruction(dummyData);
		instructionMap.put(bkt.opcode(), bkt);
		BranchIfFalseInstruction bf = new BranchIfFalseInstruction(dummyData);
		instructionMap.put(bf.opcode(), bf);
		BranchIfTrueInstruction bt = new BranchIfTrueInstruction(dummyData);
		instructionMap.put(bt.opcode(), bt);
		BranchInstruction branch = new BranchInstruction(dummyData);
		instructionMap.put(branch.opcode(), branch);
		CallSceneByIDInstruction callID = new CallSceneByIDInstruction(dummyData);
		instructionMap.put(callID.opcode(), callID);
		CallSceneByNameInstruction callName = new CallSceneByNameInstruction("dummy", 0, null);
		instructionMap.put(callName.opcode(), callName);
		DereferenceTopInstruction deref = new DereferenceTopInstruction();
		instructionMap.put(deref.opcode(), deref);
		DiscardTopInstruction discard = new DiscardTopInstruction();
		instructionMap.put(discard.opcode(), discard);
		DivideInstruction div = new DivideInstruction();
		instructionMap.put(div.opcode(), div);
		Instruction0x40 forty = new Instruction0x40(dummyData);
		instructionMap.put(forty.opcode(), forty);
		IsEqualInstruction isEq = new IsEqualInstruction();
		instructionMap.put(isEq.opcode(), isEq);
		IsGreaterThanInstruction gt = new IsGreaterThanInstruction();
		instructionMap.put(gt.opcode(), gt);
		IsGreaterThanOrEqualInstruction gte = new IsGreaterThanOrEqualInstruction();
		instructionMap.put(gte.opcode(), gte);
		IsLessThanInstruction lt = new IsLessThanInstruction();
		instructionMap.put(lt.opcode(), lt);
		IsLessThanOrEqualInstruction lte = new IsLessThanOrEqualInstruction();
		instructionMap.put(lte.opcode(), lte);
		IsNotEqualInstruction neq = new IsNotEqualInstruction();
		instructionMap.put(neq.opcode(), neq);
		IsStringEqualInstruction strEq = new IsStringEqualInstruction();
		instructionMap.put(strEq.opcode(), strEq);
		IsStringNotEqualInstruction strNeq = new IsStringNotEqualInstruction();
		instructionMap.put(strNeq.opcode(), strNeq);
		LogicalNotInstruction logicNot = new LogicalNotInstruction();
		instructionMap.put(logicNot.opcode(), logicNot);
		ModuloInstruction modulo = new ModuloInstruction();
		instructionMap.put(modulo.opcode(), modulo);
		MultiplyInstruction mul = new MultiplyInstruction();
		instructionMap.put(mul.opcode(), mul);
		NegateInstruction neg = new NegateInstruction();
		instructionMap.put(neg.opcode(), neg);
		NOPInstruction nop = new NOPInstruction();
		instructionMap.put(nop.opcode(), nop);
		PrintFInstruction printf = new PrintFInstruction(dummyData);
		instructionMap.put(printf.opcode(), printf);
		PushArrayRefItem16Instruction pari16 = new PushArrayRefItem16Instruction(dummyData);
		instructionMap.put(pari16.opcode(), pari16);
		PushArrayRefItem8Instruction pari8 = new PushArrayRefItem8Instruction(dummyData);
		instructionMap.put(pari8.opcode(), pari8);
		PushArrayRefItemRef16Instruction parir16 = new PushArrayRefItemRef16Instruction(dummyData);
		instructionMap.put(parir16.opcode(), parir16);
		PushArrayRefItemRef8Instruction parir8 = new PushArrayRefItemRef8Instruction(dummyData);
		instructionMap.put(parir8.opcode(), parir8);
		PushArrayVarItem16Instruction pavi16 = new PushArrayVarItem16Instruction(dummyData);
		instructionMap.put(pavi16.opcode(), pavi16);
		PushArrayVarItem8Instruction pavi8 = new PushArrayVarItem8Instruction(dummyData);
		instructionMap.put(pavi8.opcode(), pavi8);
		PushArrayVarItemRef16Instruction pavir16 = new PushArrayVarItemRef16Instruction(dummyData);
		instructionMap.put(pavir16.opcode(), pavir16);
		PushArrayVarItemRef8Instruction pavir8 = new PushArrayVarItemRef8Instruction(dummyData);
		instructionMap.put(pavir8.opcode(), pavir8);
		PushGlobalArrayRefItem16Instruction pgari16 = new PushGlobalArrayRefItem16Instruction(dummyData);
		instructionMap.put(pgari16.opcode(), pgari16);
		PushGlobalArrayRefItem8Instruction pgari8 = new PushGlobalArrayRefItem8Instruction(dummyData);
		instructionMap.put(pgari8.opcode(), pgari8);
		PushGlobalArrayRefItemRef16Instruction pgarir16 = new PushGlobalArrayRefItemRef16Instruction(dummyData);
		instructionMap.put(pgarir16.opcode(), pgarir16);
		PushGlobalArrayRefItemRef8Instruction pgarir8 = new PushGlobalArrayRefItemRef8Instruction(dummyData);
		instructionMap.put(pgarir8.opcode(), pgarir8);
		PushGlobalArrayVarItem16Instruction pgavi16 = new PushGlobalArrayVarItem16Instruction(dummyData);
		instructionMap.put(pgavi16.opcode(), pgavi16);
		PushGlobalArrayVarItem8Instruction pgavi8 = new PushGlobalArrayVarItem8Instruction(dummyData);
		instructionMap.put(pgavi8.opcode(), pgavi8);
		PushGlobalArrayVarItemRef16Instruction pgavir16 = new PushGlobalArrayVarItemRef16Instruction(dummyData);
		instructionMap.put(pgavir16.opcode(), pgavir16);
		PushGlobalArrayVarItemRef8Instruction pgavir8 = new PushGlobalArrayVarItemRef8Instruction(dummyData);
		instructionMap.put(pgavir8.opcode(), pgavir8);
		PushGlobalVar16Instruction pgv16 = new PushGlobalVar16Instruction(dummyData);
		instructionMap.put(pgv16.opcode(), pgv16);
		PushGlobalVar8Instruction pgv8 = new PushGlobalVar8Instruction(dummyData);
		instructionMap.put(pgv8.opcode(), pgv8);
		PushGlobalVarRef16Instruction pgvr16 = new PushGlobalVarRef16Instruction(dummyData);
		instructionMap.put(pgvr16.opcode(), pgvr16);
		PushGlobalVarRef8Instruction pgvr8 = new PushGlobalVarRef8Instruction(dummyData);
		instructionMap.put(pgvr8.opcode(), pgvr8);
		PushLiteralNum16Instruction numLit16 = new PushLiteralNum16Instruction(dummyData);
		instructionMap.put(numLit16.opcode(), numLit16);
		PushLiteralNum32Instruction numLit32 = new PushLiteralNum32Instruction(dummyData);
		instructionMap.put(numLit32.opcode(), numLit32);
		PushLiteralNum8Instruction numLit8 = new PushLiteralNum8Instruction(dummyData);
		instructionMap.put(numLit8.opcode(), numLit8);
		PushLiteralString16Instruction strLit16 = new PushLiteralString16Instruction("dummy", null);
		instructionMap.put(strLit16.opcode(), strLit16);
		PushLiteralString32Instruction strLit32 = new PushLiteralString32Instruction("dummy", null);
		instructionMap.put(strLit32.opcode(), strLit32);
		PushLiteralString8Instruction strLit8 = new PushLiteralString8Instruction("dummy", null);
		instructionMap.put(strLit8.opcode(), strLit8);
		PushVar16Instruction pv16 = new PushVar16Instruction(dummyData);
		instructionMap.put(pv16.opcode(), pv16);
		PushVar8Instruction pv8 = new PushVar8Instruction(dummyData);
		instructionMap.put(pv8.opcode(), pv8);
		PushVarRef16Instruction pvr16 = new PushVarRef16Instruction(dummyData);
		instructionMap.put(pvr16.opcode(), pvr16);
		PushVarRef8Instruction pvr8 = new PushVarRef8Instruction(dummyData);
		instructionMap.put(pvr8.opcode(), pvr8);
		ReturnInstruction ret = new ReturnInstruction();
		instructionMap.put(ret.opcode(), ret);
		StoreValInRefInstruction store = new StoreValInRefInstruction();
		instructionMap.put(store.opcode(), store);
		SubtractInstruction sub = new SubtractInstruction();
		instructionMap.put(sub.opcode(), sub);
		YieldInstruction yield = new YieldInstruction();
		instructionMap.put(yield.opcode(), yield);
	}
	
	public static List<ScriptInstruction> instructionsFromScript(FE9ScriptScene scene) {
		return instructionsFromBytes(scene.scriptBytes, scene.handler);
	}
	
	public static List<ScriptInstruction> instructionsFromBytes(byte[] scriptBytes, GCNCMBFileHandler handler) {
		List<ScriptInstruction> instructions = new ArrayList<ScriptInstruction>();
		int currentIndex = 0;
		
		while (currentIndex < scriptBytes.length) {
			byte opcode = scriptBytes[currentIndex++];
			ScriptInstruction baseInstruction = instructionMap.get(opcode);
			if (baseInstruction == null) {
				instructions.add(new UnknownInstruction(opcode));
			} else {
				byte[] argBytes = new byte[baseInstruction.numArgBytes()];
				for (int i = 0; i < baseInstruction.numArgBytes(); i++) {
					argBytes[i] = scriptBytes[currentIndex++];
				}
				
				ScriptInstruction instruction = baseInstruction.createWithArgs(argBytes, handler);
				instructions.add(instruction);
				
				if (instruction instanceof ReturnInstruction) {
					// Most scripts have some junk at the end. I'm not sure if these are important or not,
					// but they can occasionally be a real opcode with inappropriate args.
					// We'll drop them, but I'm not sure if they will be necessary for something else.
					break; 
				}
			}
		}
		
		return instructions;
	}
	
	private static class UnknownInstruction extends ScriptInstruction {
		
		byte opcode;
		
		public UnknownInstruction(byte opcode) {
			this.opcode = opcode;
		}

		@Override
		public String displayString() {
			return "UNKNOWN_INSTRUCTION_0x" + Integer.toHexString(opcode);
		}

		@Override
		public byte[] rawBytes() {
			return new byte[] {opcode};
		}

		@Override
		public byte opcode() {
			return opcode;
		}

		@Override
		public int numArgBytes() {
			return 0;
		}

		@Override
		public ScriptInstruction createWithArgs(byte[] args, GCNCMBFileHandler handler) {
			return new UnknownInstruction((byte)0xFF);
		}
		
	}

}
