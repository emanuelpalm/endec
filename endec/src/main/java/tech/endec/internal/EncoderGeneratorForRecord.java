package tech.endec.internal;

import jakarta.annotation.Nonnull;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public class EncoderGeneratorForRecord implements EncoderGenerator
{
    @Override public boolean tryGenerate(@Nonnull TypeElement element, @Nonnull ProcessorContext context)
    {
        if (element.getKind() != ElementKind.RECORD) {
            return false;
        }

        try {
            var packageElement = context.elementUtils().getPackageOf(element);
            var simpleName = element.getSimpleName();
            var resource = context.filer().createSourceFile(element.getQualifiedName() + "Encoder");
            var components = element.getRecordComponents();
            try (var output = resource.openOutputStream()) {
                output.write("""
                        package %s;
                        
                        import tech.endec.type.Encoder;
                        
                        public final class %sEncoder {
                            private %sEncoder() {}
                        
                            public static void encode(%s input, Encoder encoder)
                            {
                                var map = encoder.encodeMap(input, %d);

                        """.formatted(packageElement, simpleName, simpleName, simpleName, components.size())
                        .getBytes(StandardCharsets.UTF_8));

                for (var component : components) {
                    var name = component.getSimpleName();
                    output.write("""
                                    map.nextKey().encodeString("%s");
                                    map.nextValue().encodeString(input.%s());

                            """.formatted(name, name).getBytes(StandardCharsets.UTF_8));
                }

                output.write("""
                                map.end();
                            }
                        }
                        """.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return true;
    }
}
