package tech.endec.internal;

import jakarta.annotation.Nonnull;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EncoderGeneratorForRecord implements EncoderGenerator
{
    @Override public boolean tryGenerate(@Nonnull TypeElement element, @Nonnull ProcessorContext context)
            throws IOException
    {
        if (element.getKind() != ElementKind.RECORD) {
            return false;
        }

        var packageElement = context.getPackageOf(element);
        var simpleName = element.getSimpleName();
        var sourceFile = context.createSourceFile(element.getQualifiedName() + "Encoder", element);
        var components = element.getRecordComponents();
        try (var output = sourceFile.openOutputStream()) {
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

        return true;
    }
}
