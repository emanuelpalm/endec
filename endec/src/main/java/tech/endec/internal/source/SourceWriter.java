package tech.endec.internal.source;

import jakarta.annotation.Nonnull;

public class SourceWriter
{
    private final @Nonnull StringBuilder builder = new StringBuilder(512);

    private boolean isUsed = false;
    private int currentDepth = 0;
    private int currentLineNumber = 0;

    public @Nonnull Scope scope()
    {
        if (isUsed) {
            throw new IllegalStateException("Root scope already used");
        }
        isUsed = true;
        return new Scope(1);
    }

    public @Nonnull SourceFile toFile(@Nonnull String name)
    {
        return new SourceFile(name, builder.toString());
    }

    public class Line
    {
        private final int depth;
        private final int lineNumber;

        private boolean isEmpty = true;

        protected Line(int depth)
        {
            if (currentLineNumber++ > 0) {
                builder.append(System.lineSeparator());
            }

            this.depth = depth;
            this.lineNumber = currentLineNumber;
        }

        public @Nonnull Line write(char ch)
        {
            throwIfOutOfSequence();
            indentIfEmpty();
            builder.append(ch);
            return this;
        }

        public @Nonnull Line write(@Nonnull CharSequence string)
        {
            throwIfOutOfSequence();
            indentIfEmpty();
            builder.append(string);
            return this;
        }

        public @Nonnull Line write(@Nonnull Object object)
        {
            throwIfOutOfSequence();
            indentIfEmpty();
            builder.append(object);
            return this;
        }

        private void indentIfEmpty()
        {
            if (isEmpty) {
                isEmpty = false;
                builder.repeat(' ', Math.multiplyExact(depth, 4));
            }
        }

        private void throwIfOutOfSequence()
        {
            if (lineNumber != currentLineNumber) {
                throw new IllegalStateException("Line used out of sequence");
            }
        }
    }

    public class Scope
    {
        private final int depth;

        private boolean isEnded = false;

        protected Scope(int depth)
        {
            currentDepth += 1;

            this.depth = depth;
        }

        public @Nonnull Line line()
        {
            throwIfOutOfSequence();
            return new Line(depth);
        }

        public @Nonnull Scope scope()
        {
            throwIfOutOfSequence();
            return new Scope(depth + 1);
        }

        public void end()
        {
            throwIfOutOfSequence();
            currentDepth -= 1;
            isEnded = true;
        }

        private void throwIfOutOfSequence()
        {
            if (isEnded) {
                throw new IllegalStateException("Scope already ended");
            }
            if (depth != currentDepth) {
                throw new IllegalStateException("Scope used out of sequence");
            }
        }
    }
}
