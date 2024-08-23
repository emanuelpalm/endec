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
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class EndecProcessor extends AbstractProcessor {
    private final HashSet<TypeElement> decodables = new HashSet<>();
    private final HashSet<TypeElement> encodables = new HashSet<>();

    private EndecWriter writer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        writer = new EndecWriter(processingEnv.getFiler(), messager);
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
        if (element.getKind() == ElementKind.RECORD) {
            processRecord(annotation, (TypeElement) element);
        } else {
            var annotationName = annotation.getSimpleName();
            var elementKind = element.getKind();
            var message = "Only records may be annotated with @%s, but %s is a %s"
                    .formatted(annotationName, element, elementKind);

            messager.printError(message, element);
        }
    }

    private void processRecord(TypeElement annotation, TypeElement element) {
        if (isDecodable(annotation, element)) {
            System.out.printf("Create decoder for %s%n", element);
        }
        if (isEncodable(annotation, element)) {
            System.out.printf("Create encoder for %s%n", element);
            var writable = new EndecJavaClass(element, element.getSimpleName() + "$Encoder.java", "se.example");
            writer.write(writable);
        }
    }

    private boolean isDecodable(TypeElement annotation, TypeElement element) {
        if (notEqual(annotation, Codable.class) && notEqual(annotation, Decodable.class)) {
            return false;
        }
        if (!decodables.add(element)) {
            messager.printWarning("This record should only be annotated " +
                    "with one of @Codable and @Decodable", element);
            return false;
        }
        return true;
    }

    private boolean isEncodable(TypeElement annotation, TypeElement element) {
        if (notEqual(annotation, Codable.class) && notEqual(annotation, Encodable.class)) {
            return false;
        }
        if (!encodables.add(element)) {
            messager.printWarning("This record should only be annotated " +
                    "with one of @Codable and @Encodable", element);
            return false;
        }
        return true;
    }

    private boolean notEqual(TypeElement annotationType, Class<?> annotationClass) {
        var typeName = annotationType.getQualifiedName();
        var className = annotationClass.getName();
        return typeName == null || !typeName.contentEquals(className);
    }
}
