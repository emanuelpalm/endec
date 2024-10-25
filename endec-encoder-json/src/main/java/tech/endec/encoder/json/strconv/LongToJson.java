// Copyright 2014 The Rust Project Developers; adapted by Emanuel Palm in 2024
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// See also the COPYRIGHT file at http://rust-lang.org/COPYRIGHT.

// The code serving as foundation for this implementation can be found at
// https://github.com/rust-lang/rust/blob/b8214dc6c6fc20d0a660fb5700dca9ebf51ebe89/src/libcore/fmt/num.rs#L188-L266.

package tech.endec.encoder.json.strconv;

import jakarta.annotation.Nonnull;
import tech.endec.encoder.EncoderOutput;

public final class LongToJson
{
    private static final byte[] TABLE_DECIMAL_DIGIT_PAIRS = {
            '0', '0', '0', '1', '0', '2', '0', '3', '0', '4', '0', '5', '0', '6', '0', '7', '0', '8', '0', '9',
            '1', '0', '1', '1', '1', '2', '1', '3', '1', '4', '1', '5', '1', '6', '1', '7', '1', '8', '1', '9',
            '2', '0', '2', '1', '2', '2', '2', '3', '2', '4', '2', '5', '2', '6', '2', '7', '2', '8', '2', '9',
            '3', '0', '3', '1', '3', '2', '3', '3', '3', '4', '3', '5', '3', '6', '3', '7', '3', '8', '3', '9',
            '4', '0', '4', '1', '4', '2', '4', '3', '4', '4', '4', '5', '4', '6', '4', '7', '4', '8', '4', '9',
            '5', '0', '5', '1', '5', '2', '5', '3', '5', '4', '5', '5', '5', '6', '5', '7', '5', '8', '5', '9',
            '6', '0', '6', '1', '6', '2', '6', '3', '6', '4', '6', '5', '6', '6', '6', '7', '6', '8', '6', '9',
            '7', '0', '7', '1', '7', '2', '7', '3', '7', '4', '7', '5', '7', '6', '7', '7', '7', '8', '7', '9',
            '8', '0', '8', '1', '8', '2', '8', '3', '8', '4', '8', '5', '8', '6', '8', '7', '8', '8', '8', '9',
            '9', '0', '9', '1', '9', '2', '9', '3', '9', '4', '9', '5', '9', '6', '9', '7', '9', '8', '9', '9',
    };

    private static final byte[] STRING_MIN_VALUE = {
            '-', '9', '2', '2', '3', '3', '7', '2', '0', '3', '6', '8', '5', '4', '7', '7', '5', '8', '0', '8'};

    private LongToJson() {}

    public static void format(long value, @Nonnull EncoderOutput output)
    {
        final boolean isNegative;
        if (value < 0) {
            // Because long is a two's complement representation, the absolute
            // value of Long.MIN_VALUE is one larger than Long.MAX_VALUE. We
            // deal with the special case of `value` being equal to it by just
            // writing out a constant string and returning.
            if (value == Long.MIN_VALUE) {
                output.write(STRING_MIN_VALUE);
                return;
            }
            value = -value;
            isNegative = true;
        } else {
            isNegative = false;
        }

        var buffer = new byte[20];

        // We start at the end of the output buffer and move downwards.
        var index = buffer.length;

        // Eagerly decode 4 digits at a time.
        while (value >= 10000) {
            var remainder = (value % 10000);
            value /= 10000;

            var d1 = (int) (remainder / 100) << 1;
            var d2 = (int) (remainder % 100) << 1;
            index -= 4;

            buffer[index] = TABLE_DECIMAL_DIGIT_PAIRS[d1];
            buffer[index + 1] = TABLE_DECIMAL_DIGIT_PAIRS[d1 + 1];
            buffer[index + 2] = TABLE_DECIMAL_DIGIT_PAIRS[d2];
            buffer[index + 3] = TABLE_DECIMAL_DIGIT_PAIRS[d2 + 1];
        }

        // If we reach here, `value <= 9999`, which means that we will need to
        // write at most 4 more digits.

        // Decode 2 more digits, if >2 digits.
        if (value >= 100) {
            var d1 = (int) (value % 100) << 1;
            value /= 100;
            index -= 2;

            buffer[index] = TABLE_DECIMAL_DIGIT_PAIRS[d1];
            buffer[index + 1] = TABLE_DECIMAL_DIGIT_PAIRS[d1 + 1];
        }

        // Decode last 1 or 2 digits.
        if (value < 10) {
            index -= 1;
            buffer[index] = (byte) (((byte) '0') + value);
        } else {
            var d1 = (int) value << 1;
            index -= 2;

            buffer[index] = TABLE_DECIMAL_DIGIT_PAIRS[d1];
            buffer[index + 1] = TABLE_DECIMAL_DIGIT_PAIRS[d1 + 1];
        }

        if (isNegative) {
            index -= 1;
            buffer[index] = (byte) '-';
        }

        output.write(buffer, index, buffer.length - index);
    }
}
