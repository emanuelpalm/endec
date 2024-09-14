package tech.endec.internal;

import jakarta.annotation.Nonnull;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.PrintWriter;

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
        try (var writer = new PrintWriter(sourceFile.openOutputStream())) {
            writer.format("""
                    package %1$s;
                    
                    import tech.endec.type.Encoder;
                    
                    import java.util.Objects;
                    
                    public final class %2$sEncoder {
                        private %2$sEncoder() {}
                    
                        public static void encode(%2$s input, Encoder encoder)
                        {
                            Objects.requireNonNull(input);
                            Objects.requireNonNull(encoder);
                    
                            var map = encoder.encodeMap(input, %3$d);
                    
                    """, packageElement, simpleName, components.size());
            for (var component : components) {
                var name = component.getSimpleName();
                writer.format("""
                                map.nextKey().encodeString("%1$s");
                                map.nextValue().encodeString(input.%1$s());
                        
                        """, name);
            }
            writer.print("""
                            map.end();
                        }
                    }
                    """);
        }

        return true;
    }
}
