package util.recordkeeper.fe9;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ChangelogBuilder {
	
	private String title;
	
	private List<ChangelogStyleRule> styles;
	private List<ChangelogElement> elements;
	
	public ChangelogBuilder() {
		styles = new ArrayList<ChangelogStyleRule>();
		elements = new ArrayList<ChangelogElement>();
	}
	
	public void setDocumentTitle(String title) {
		this.title = title;
	}
	
	public void addElement(ChangelogElement element) {
		elements.add(element);
	}
	
	public void addStyle(ChangelogStyleRule style) {
		styles.add(style);
	}
	
	public boolean writeToPath(String path) {
		try {
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path), Charset.forName("UTF-8").newEncoder());
			writer.write("<html>\n<head>\n<meta charset=\"UTF-8\">\n");
			if (styles.size() > 0) {
				writer.write("<style>\n");
				for (ChangelogStyleRule styleRule : styles) {
					writer.write(styleRule.build() + "\n");
				}
				writer.write("</style>\n");
			}
			writer.write("</head>\n<body>\n");
			writer.write("<h1 id=\"document-title\">" + title + "</h1>\n");
			for (ChangelogElement element : elements) {
				writer.write(element.build() + "\n");
			}
			writer.write("</body></html>\n");
			writer.close();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
