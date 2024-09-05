import tech.endec.internal.EncodableProcessor;

module tech.endec {
    exports tech.endec;

    provides javax.annotation.processing.Processor with EncodableProcessor;

    requires jakarta.annotation;
    requires java.compiler;
}
