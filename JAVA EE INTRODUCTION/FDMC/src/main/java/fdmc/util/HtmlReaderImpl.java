package fdmc.util;

import java.io.*;

public class HtmlReaderImpl implements HtmlReader {

    @Override
    public String readHtmlFile(String htmlFilePath) throws IOException {
        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(
                                        new File(htmlFilePath))));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null && line.length() > 0) {
            sb.append(line).append(System.lineSeparator());
        }
            return sb.toString().trim();
    }
}
