package random.general;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RelativeValueMapper {
	
	public static List<Integer> mappedValues(List<Integer> reference, List<Integer> input) {
		if (reference.size() != input.size()) { return null; }
		if (reference.size() == 0) { return new ArrayList<Integer>(); }
		
		int size = reference.size();
		
		// We need to sort the reference values so that we know the order to assign.
		// Sort the written values from lowest to highest.
		List<Integer> sortedReference = reference.stream().sorted(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return Integer.compare(o1, o2);
			}
		}).collect(Collectors.toList());
		
		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			indices.add(i);
		}
		
		// Sort the indices based on the values at those indices from lowest to highest.
		List<Integer> writeOrder = indices.stream().sorted(new Comparator<Integer>() {
			@Override
			public int compare(Integer index1, Integer index2) {
				int result = Integer.compare(input.get(index1), input.get(index2));
				if (result == 0) {
					result = Integer.compare(index1, index2);
				}
				
				return result;
			}
		}).collect(Collectors.toList());
		
		List<Integer> result = new ArrayList<Integer>(input);
		for (int i = 0; i < size; i++) {
			int valueToWrite = sortedReference.get(i);
			int indexToWriteTo = writeOrder.get(i);
			result.set(indexToWriteTo, valueToWrite);
		}
		
		return result;
	}

}
