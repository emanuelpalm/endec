package tech.endec.internal;

import jakarta.annotation.Nonnull;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("tech.endec.Encodable")
@SupportedSourceVersion(SourceVersion.RELEASE_23)
public class EncodableProcessor extends AbstractProcessor
{
    private final @Nonnull List<EncoderGenerator> encoderGenerators = List.of(
            new EncoderGeneratorForRecord());

    private final @Nonnull HashSet<Integer> identityHashesOfAlreadyProcessedElements = new HashSet<>();

    private ProcessorContext context;

    @Override
    public synchronized void init(@Nonnull ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        context = new ProcessorContext(
                processingEnv.getFiler(),
                processingEnv.getMessager(),
                processingEnv.getElementUtils());
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
        try {
            for (var encoderGenerator : encoderGenerators) {
                if (encoderGenerator.tryGenerate(element, context)) {
                    return;
                }
            }
            context.createReport(ProcessorIssue.ENCODER_GENERATOR_UNAVAILABLE_FOR_TYPE)
                    .element(element)
                    .submit();
        }
        catch (Exception e) {
            context.createReport(ProcessorIssue.ENCODER_GENERATOR_EXCEPTION_CAUGHT)
                    .element(element)
                    .throwable(e)
                    .submit();
        }
    }
}
