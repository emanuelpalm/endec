import tech.endec.annotation.internal.EncodableProcessor;

module tech.endec {
    exports tech.endec.annotation;

    provides javax.annotation.processing.Processor with EncodableProcessor;

    requires jakarta.annotation;
    requires java.compiler;
}
