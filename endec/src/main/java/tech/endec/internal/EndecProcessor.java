package tech.endec.internal;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@SupportedAnnotationTypes("tech.endec.*")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class EndecProcessor extends AbstractProcessor
{
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        for (var annotation : annotations) {
            var elements = roundEnv.getElementsAnnotatedWith(annotation);
            for (var element : elements) {
                if (element.getKind() != ElementKind.RECORD) {
                    throw new EndecCompileException(
                        "Only records may be annotated with @%s, but %s is a %s"
                            .formatted(
                                annotation.getSimpleName(),
                                element, element.getKind()));
                }
                processRecord(annotation, (TypeElement) element);
            }
        }
        return true;
    }

    private void processRecord(TypeElement annotation, TypeElement element)
    {
        try (var writer = new PrintWriter(filer.createResource(
                StandardLocation.SOURCE_OUTPUT,
                "tech.endec.example.out",
                element.getSimpleName() + "$Hello.java")
            .openWriter())) {

            writer.printf("public class %s$Hello {%n", element.getSimpleName());
            for (var c : element.getRecordComponents()) {
                writer.printf("public int %s = 0;%n", c.getSimpleName());
            }
            writer.println("}");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
