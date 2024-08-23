package tech.endec.internal;

import javax.lang.model.element.Element;
import java.io.IOException;
import java.io.Writer;

public interface EndecWritable {
    Element element();

    CharSequence filename();

    CharSequence moduleAndPackagePath();

    void write(Writer writer) throws IOException;
}
