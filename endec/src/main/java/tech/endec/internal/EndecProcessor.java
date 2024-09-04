package tech.endec.internal;

import tech.endec.Codable;
import tech.endec.Decodable;
import tech.endec.Encodable;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

@SupportedAnnotationTypes("tech.endec.*")
@SupportedSourceVersion(SourceVersion.RELEASE_22)
public class EndecProcessor extends AbstractProcessor {
    private final HashSet<Integer> identityHashesOfAlreadyProcessedElements = new HashSet<>();

    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (var annotation : annotations) {
            var elements = roundEnv.getElementsAnnotatedWith(annotation);
            for (var element : elements) {
                processElement(annotation, element);
            }
        }
        return true;
    }

    private void processElement(TypeElement annotation, Element element) {
        if (element.getKind() != ElementKind.RECORD) {
            messager.printError("Only records may be annotated with @%s, but %s is a %s"
                    .formatted(annotation.getSimpleName(), element, element.getKind()), element);
            return;
        }

        var identityHash = System.identityHashCode(element);
        if (identityHashesOfAlreadyProcessedElements.add(identityHash)) {
            processRecord((TypeElement) element);
        }
    }

    private void processRecord(TypeElement element) {
        var isCodable = element.getAnnotation(Codable.class) != null;
        var isDecodable = element.getAnnotation(Decodable.class) != null;
        var isEncodable = element.getAnnotation(Encodable.class) != null;

        if (isCodable) {
            if (isDecodable) {
                messager.printNote("The @Decodable annotation becomes unnecessary when @Codable is used", element);
            }
            if (isEncodable) {
                messager.printNote("The @Encodable annotation becomes unnecessary when @Codable is used", element);
            }
        }

        var generator = new EndecGenerator(element);
        if (isCodable || isDecodable) {
            generator.generateDecoder();
        }
        if (isCodable || isEncodable) {
            generator.generateEncoder();
        }
        generator.write(filer);
    }
}
