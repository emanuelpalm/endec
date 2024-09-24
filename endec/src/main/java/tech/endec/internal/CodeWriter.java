package tech.endec.internal;

import jakarta.annotation.Nonnull;

public class CodeWriter
{
    private final @Nonnull StringBuilder builder;

    private boolean isUsed = false;
    private int lineCount = 0;
    private int scopeDepth = 0;

    public CodeWriter(@Nonnull StringBuilder builder)
    {
        this.builder = builder;
    }

    public @Nonnull Scope scope()
    {
        if (isUsed) {
            throw new IllegalStateException("Root scope already used");
        }
        isUsed = true;
        return new Scope(1);
    }

    public class Line
    {
        private final int depth;
        private final int lineNumber;

        private boolean isEmpty = true;

        protected Line(int depth)
        {
            if (lineCount++ > 0) {
                builder.append(System.lineSeparator());
            }

            this.depth = depth;
            this.lineNumber = lineCount;
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
            if (lineNumber != lineCount) {
                throw new IllegalStateException("Line used out of sequence");
            }
        }
    }

    public class Scope
    {
        private final int depth;

        protected Scope(int depth)
        {
            scopeDepth += 1;

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
            scopeDepth -= 1;
        }

        private void throwIfOutOfSequence()
        {
            if (scopeDepth != depth) {
                throw new IllegalStateException("Scope used out of sequence");
            }
        }
    }
}
