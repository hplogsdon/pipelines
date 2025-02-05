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

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;

public class PrintElements<T> extends PTransform<PCollection<T>, PDone> {

  public static <T> PrintElements<T> of() {
    return new PrintElements<>();
  }

  @Override
  public PDone expand(PCollection<T> input) {
    input.apply(ParDo.of(new LogResultsFn<>()));
    return PDone.in(input.getPipeline());
  }

  private static class LogResultsFn<T> extends DoFn<T, Void> {
    @ProcessElement
    public void process(@Element T element) {
      System.err.println(element);
    }
  }
}
