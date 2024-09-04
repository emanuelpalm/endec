package tech.endec.internal;

import jakarta.annotation.Nonnull;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@SupportedAnnotationTypes({
        "tech.endec.Encodable",
        "tech.endec.Decodable",
})
@SupportedSourceVersion(SourceVersion.RELEASE_22)
public class EndecProcessor extends AbstractProcessor
{
    private final HashSet<Integer> identityHashesOfAlreadyProcessedElements = new HashSet<>();

    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(@Nonnull ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
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

        var record = new EndecElement.Record((TypeElement) element);
        try {
            var code = new StringBuilder();

            for (var component : record.components()) {
                var name = component.name();
                code.append("        map.putString(\"").append(name).append("\"); ");
                code.append("map.putString(input.").append(name).append("());\n");
            }

            var location = StandardLocation.SOURCE_OUTPUT;
            var moduleAndPackage = record.moduleAndPackage();
            var simpleName = record.simpleName();
            var resource = filer.createResource(location, moduleAndPackage, simpleName + "Encoder.java");
            try (var output = resource.openOutputStream()) {
                output.write("""
                        package %s;
                        
                        import tech.endec.Encoder;
                        
                        public final class %sEncoder {
                            private %sEncoder() {}
                        
                            public static void encode(%s input, Encoder encoder)
                            {
                                var map = encoder.encodeMap();

                        %s
                                map.endMap();
                            }
                        }
                        """.formatted(moduleAndPackage, simpleName, simpleName, simpleName, code)
                        .getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
