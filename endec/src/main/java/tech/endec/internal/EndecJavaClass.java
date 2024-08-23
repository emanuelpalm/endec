package tech.endec.internal;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.PrintWriter;
import java.io.Writer;

public record EndecJavaClass(
        Element element,
        CharSequence filename,
        CharSequence moduleAndPackagePath)
        implements EndecWritable {
    @Override
    public void write(Writer writer) {
        try (var printer = new PrintWriter(writer)) {
            printer.printf("public class %s$Encoder {%n", element.getSimpleName());
            if (element instanceof TypeElement e) {
                for (var c : e.getRecordComponents()) {
                    printer.printf("public int %s = 0;%n", c.getSimpleName());
                }
            }
            printer.println("}");
        }
    }
}
