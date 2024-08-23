package tech.endec.internal;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.tools.StandardLocation;
import java.io.IOException;

public class EndecWriter {
    private final Filer filer;
    private final Messager messager;

    public EndecWriter(Filer filer, Messager messager) {
        this.filer = filer;
        this.messager = messager;
    }

    public void write(EndecWritable writable) {
        try (var writer = filer.createResource(
                        StandardLocation.SOURCE_OUTPUT,
                        writable.moduleAndPackagePath(),
                        writable.filename())
                .openWriter()) {

            writable.write(writer);

        } catch (IOException e) {
            messager.printError(
                    "Failed to generate %s; %s".formatted(writable.filename(), e),
                    writable.element());
        }
    }
}
