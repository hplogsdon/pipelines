/**
 * Copyright 2021-2024 Packt Publishing Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hplogsdon.beam.util;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TimestampedValue;
import org.joda.time.Instant;
import org.junit.jupiter.api.Test;

public class ToMetricTest {

  @Test
  public void testComputationBatch() {
    Pipeline p = Pipeline.create();
    Instant now = Instant.ofEpochMilli(1234567890000L);
    Position initial = Position.random(now.getMillis());
    PCollection<KV<String, Position>> input =
        p.apply(
            Create.timestamped(
                    TimestampedValue.of(KV.of("foo", initial), now),
                    TimestampedValue.of(
                        KV.of("foo", initial.move(1, 1, 3, 120000)), now.plus(120000)))
                .withCoder(KvCoder.of(StringUtf8Coder.of(), new PositionCoder())));
  }
}
