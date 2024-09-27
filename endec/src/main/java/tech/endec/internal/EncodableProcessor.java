package tech.endec.internal;

import jakarta.annotation.Nonnull;
import tech.endec.internal.audit.Audit;
import tech.endec.internal.audit.AuditCode;
import tech.endec.internal.generator.EncoderGenerator;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

@SupportedAnnotationTypes("tech.endec.Encodable")
@SupportedSourceVersion(SourceVersion.RELEASE_23)
public class EncodableProcessor extends AbstractProcessor
{
    private final @Nonnull HashSet<Integer> identityHashesOfAlreadyProcessedElements = new HashSet<>();

    private Audit audit;
    private Filer filer;
    private EncoderGenerator generator;

    @Override
    public synchronized void init(@Nonnull ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        audit = new Audit(processingEnv.getMessager());
        filer = processingEnv.getFiler();
        generator = new EncoderGenerator(
                audit,
                processingEnv.getElementUtils(),
                processingEnv.getTypeUtils());
    }

    @Override
    public boolean process(@Nonnull Set<? extends TypeElement> annotations, @Nonnull RoundEnvironment roundEnv)
    {
        for (var annotation : annotations) {
            for (var element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (isNotAlreadyProcessed(element)) {
                    processElement((TypeElement) element);
                }
            }
        }
        return true;
    }

    private boolean isNotAlreadyProcessed(@Nonnull Element element)
    {
        var identityHash = System.identityHashCode(element);
        return identityHashesOfAlreadyProcessedElements.add(identityHash);
    }

    private void processElement(@Nonnull TypeElement element)
    {
        var file = generator.generate(element);
        try {
            var fileObject = filer.createSourceFile(file.name(), element);
            try (var output = fileObject.openOutputStream()) {
                output.write(file.body().getBytes());
            }
        }
        catch (Exception e) {
            audit.report(AuditCode.ENCODER_GENERATOR_EXCEPTION_CAUGHT.toIssue()
                    .element(element)
                    .throwable(e));
        }
    }
}
