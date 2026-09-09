package util.recordkeeper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecordBuilder {
    private final String outputPath;
    private StringBuilder output = new StringBuilder();

    public RecordBuilder(String path) {
        this.outputPath = path;
    }

    public RecordBuilder appendBasicTable(Set<Map.Entry<String, String>> entrySet) {
        output.append("<table>\n");
        entrySet.forEach(e -> {
            output.append("<tr><td>").append(e.getKey()).append("</td><td>").append(e.getValue()).append("</td></tr>\n");
        });
        output.append("</table>\n");
        return this;
    }

    public RecordBuilder appendTOC(List<String> entries, int columns) {
        output.append("<table>\n");
        int currentColumn = 0;
        for (String entry : entries) {
            if (currentColumn == 0)
                output.append("<tr>\n");

            output.append(String.format("<td><a href=\"#%s\">%s</a></td>\n", keyFromString(entry), entry));
            currentColumn += 1;
            if (currentColumn == columns) {
                output.append("</tr>\n");
                currentColumn = 0;
            }
        }
        output.append("</table>\n");
        return this;
    }

    public RecordBuilder appendLinkToTOC(String category) {
        output.append(String.format("<a href=\"#%s\">Back to %s</a>\n", keyFromString(category), category));
        return this;
    }

    public RecordBuilder appendEntryComparison(RecordEntry recordCategory) {
        output.append("<table>\n");
        for (String key : recordCategory.getInfoKeys()) {
            RecordInformation entry = recordCategory.getInfo(key);
            String oldValue = entry.originalValue;
            String newValue = entry.updatedValue;
            output.append("<tr>\n").append(String.format("<td>%s</td>\n", key))
                    .append(String.format("<td>%s</td>\n", oldValue != null ? oldValue : "(null)"))
                    .append(String.format("<td>%s</td>\n", newValue != null ? newValue : "(null)"))
                    .append(entry.additionalInfo != null ? String.format("<td>%s</td>",  entry.additionalInfo) : "")
                    .append("</tr>");
        }
        output.append("</table>\n");
        return this;
    }

    public RecordBuilder appendHorizontalSpacer() {
        output.append("<br><hr><br>\n");
        return this;
    }

    public RecordBuilder appendUnorderedList(List<String> values) {
        output.append("<ul>");
        values.forEach(v -> output.append("<li>").append(v).append("</li>\n"));
        output.append("</ul>");
        return this;
    }

    public RecordBuilder appendLiteral(String literal) {
        output.append(literal);
        return this;
    }

    public RecordBuilder appendSectionHeader(String title, int size) {
        assert (size > 0 && size < 7);
        String startTag = String.format("<h%d ", size);
        String endTag = String.format("<h%d/>", size);
        output.append(startTag).append("id=\"").append(keyFromString(title)).append("\">").append(title).append(endTag).append("<br>\n");
        return this;
    }

    public RecordBuilder buildHeader(String title) {
        output.append("<html><meta http-equiv=\"Content-Type\" content = \"text/html; charset=utf-8\" /><head><style>\n")
                .append("table, th, td {\n\tborder: 1px solid black;\n}\n")
                .append(".notes {\n\twidth: 66%;\n\tmargin: auto;\n}\n")
                .append("</style></head><body>\n")
                .append("<center><h1><p>Changelog for ").append(title).append("</p></h1><br>\n")
                .append("<hr>\n");
        return this;
    }

    public void write() {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(outputPath), StandardCharsets.UTF_8.newEncoder())) {
            writer.write(output.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String keyFromString(String string) {
        return string.replace(' ', '_');
    }
}