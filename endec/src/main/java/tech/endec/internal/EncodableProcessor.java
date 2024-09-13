package tech.endec.internal;

import jakarta.annotation.Nonnull;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@SupportedAnnotationTypes("tech.endec.Encodable")
@SupportedSourceVersion(SourceVersion.RELEASE_22)
public class EncodableProcessor extends AbstractProcessor
{
    private final HashSet<Integer> identityHashesOfAlreadyProcessedElements = new HashSet<>();

    private Filer filer;
    private Messager messager;
    private Elements elements;

    @Override
    public synchronized void init(@Nonnull ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elements = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(@Nonnull Set<? extends TypeElement> annotations, @Nonnull RoundEnvironment roundEnv)
    {
        for (var annotation : annotations) {
            for (var element : roundEnv.getElementsAnnotatedWith(annotation)) {
                processElement(element);
            }
        }
        return true;
    }

    private void processElement(@Nonnull Element element)
    {
        var identityHash = System.identityHashCode(element);
        if (!identityHashesOfAlreadyProcessedElements.add(identityHash)) {
            return;
        }

        if (element.getKind() != ElementKind.RECORD) {
            messager.printError("Only records are supported, but %s is a %s"
                    .formatted(element, element.getKind()), element);
            return;
        }

        try {
            var typeElement = (TypeElement) element;
            var packageElement = elements.getPackageOf(element);
            var simpleName = element.getSimpleName();
            var resource = filer.createSourceFile(typeElement.getQualifiedName() + "Encoder");
            var components = typeElement.getRecordComponents();
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
    }
}
