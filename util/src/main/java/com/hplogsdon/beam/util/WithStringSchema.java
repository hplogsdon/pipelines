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

import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.Row;
import org.apache.beam.sdk.values.TypeDescriptors;

public class WithStringSchema extends PTransform<PCollection<String>, PCollection<String>> {

  public static WithStringSchema of(String field) {
    return new WithStringSchema(field);
  }

  private final String name;
  private final Schema schema;

  private WithStringSchema(String name) {
    this.name = name;
    this.schema = Schema.builder().addStringField(name).build();
  }

  @Override
  public PCollection<String> expand(PCollection<String> input) {
    return input
        .apply(MapElements.into(TypeDescriptors.strings()).via(e -> e))
        .setSchema(
            schema,
            TypeDescriptors.strings(),
            s -> Row.withSchema(schema).attachValues(s),
            r -> r.getString(name));
  }
}
