package util.recordkeeper;

import java.util.ArrayList;
import java.util.List;

public class ChangelogTable implements ChangelogElement {
	
	private String identifier;
	private String[] headers;
	private List<String[]> contents;
	int columnCount;
	
	public ChangelogTable(int numColumns, String[] headers, String identifier) {
		this.headers = headers;
		contents = new ArrayList<String[]>();
		this.identifier = identifier;
		columnCount = numColumns;
	}

	public String getContents(int row, int column) {
		String[] rowContents = contents.get(row);
		if (rowContents.length < column) {
			return rowContents[column];
		}
		
		return null;
	}
	
	public void setContents(int row, int column, String content) {
		if (contents.size() < row) {
			String[] rowContents = contents.get(row);
			if (rowContents.length < column) {
				rowContents[column] = content;
			}
		}
	}
	
	public void addRow(String[] rowContents) {
		if (rowContents == null) { rowContents = new String[columnCount]; }
		else { assert rowContents.length == columnCount; }
		contents.add(rowContents);
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public String build() {
		StringBuilder sb = new StringBuilder();
		sb.append("<table id=\"" + identifier + "\" class=\"changelog-table\">");
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
				String data = contents.get(i)[j];
				if (data != null) { sb.append(data); }
				sb.append("</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		
		return sb.toString();
	}
}
