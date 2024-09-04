package tech.endec.internal;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import javax.lang.model.element.*;
import java.util.ArrayList;
import java.util.List;

public sealed interface EndecElement
{
    record Record(@Nonnull TypeElement element) implements EndecElement
    {
        public @Nonnull List<RecordComponent> components()
        {
            var components = element.getRecordComponents();
            var result = new ArrayList<RecordComponent>(components.size());
            for (var component : components) {
                result.add(new RecordComponent(component));
            }
            return result;
        }

        public @Nullable CharSequence moduleAndPackage()
        {
            var parent = element.getEnclosingElement();
            while (parent != null && parent.getKind() != ElementKind.PACKAGE) {
                parent = parent.getEnclosingElement();
            }
            if (parent instanceof PackageElement packageElement) {
                return packageElement.getQualifiedName();
            }
            return "";
        }

        public CharSequence simpleName() { return element.getSimpleName(); }
    }

    record RecordComponent(@Nonnull RecordComponentElement element) implements EndecElement
    {
        public CharSequence name() { return element.getSimpleName(); }
    }
}
