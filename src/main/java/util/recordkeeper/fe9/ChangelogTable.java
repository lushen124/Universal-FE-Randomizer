package util.recordkeeper.fe9;

import java.util.ArrayList;
import java.util.List;

public class ChangelogTable implements ChangelogElement {
	
	private String identifier;
	private String[] headers;
	private List<String[]> contents;
	private List<ChangelogElement[]> elements;
	int columnCount;
	
	private List<String> classes;
	
	public ChangelogTable(int numColumns, String[] headers, String identifier) {
		this.headers = headers;
		contents = new ArrayList<String[]>();
		elements = new ArrayList<ChangelogElement[]>();
		this.identifier = identifier;
		columnCount = numColumns;
		
		classes = new ArrayList<String>();
	}

	public String getContents(int row, int column) {
		String[] rowContents = contents.get(row);
		if (column < rowContents.length) {
			return rowContents[column];
		}
		
		return null;
	}
	
	public void setContents(int row, int column, String content) {
		if (row < contents.size()) {
			String[] rowContents = contents.get(row);
			if (column < rowContents.length) {
				rowContents[column] = content;
			}
		}
	}
	
	public ChangelogElement getElement(int row, int column) {
		ChangelogElement[] elementArray = elements.get(row);
		if (column < elementArray.length) {
			return elementArray[column];
		}
		
		return null;
	}
	
	public void setElement(int row, int column, ChangelogElement element) {
		if (row < elements.size()) {
			ChangelogElement[] array = elements.get(row);
			if (column < array.length) {
				array[column] = element;
			}
		}
	}
	
	public void addRow(String[] rowContents) {
		if (rowContents == null) { rowContents = new String[columnCount]; }
		else { assert rowContents.length == columnCount; }
		contents.add(rowContents);
		elements.add(new ChangelogElement[columnCount]);
	}
	
	public void addRow(ChangelogElement[] rowElements) {
		if (rowElements == null) { rowElements = new ChangelogElement[columnCount]; }
		else { assert rowElements.length == columnCount; }
		contents.add(new String[columnCount]);
		elements.add(rowElements);
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String build() {
		StringBuilder sb = new StringBuilder();
		sb.append("<a id=\"anchor-" + identifier + "\"></a>");
		sb.append("<table id=\"" + identifier + "\" class=\"changelog-table");
		for (String elementClass : classes) {
			sb.append(" " + elementClass);
		}
		sb.append("\">");
		if (headers != null) {
			sb.append("<tr class=\"changelog-table-header-row\">");
			for (int i = 0; i < headers.length; i++) {
				sb.append("<th class=\"changelog-table-header\">");
				String header = headers[i];
				if (header != null) { sb.append(header); }
				sb.append("</th>");
			}
			sb.append("</tr>");
		}
		for (int i = 0; i < contents.size(); i++) {
			sb.append("<tr class=\"changelog-table-data-row\">");
			for (int j = 0; j < contents.get(i).length; j++) {
				sb.append("<td class=\"changelog-table-data\">");
				ChangelogElement element = elements.get(i)[j];
				String data = element != null ? element.build() : contents.get(i)[j];
				if (data != null) { sb.append(data); }
				sb.append("</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		
		return sb.toString();
	}

	@Override
	public List<String> getClasses() {
		return classes;
	}

	@Override
	public void addClass(String elementClass) {
		classes.add(elementClass);
	}
}
