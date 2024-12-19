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
package com.hplogsdon.beam.chapter1;

import com.hplogsdon.beam.util.PrintElements;
import com.hplogsdon.beam.util.Tokenize;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Create;

public class FirstPipeline {
  public static void main(String[] args) throws IOException, NullPointerException {
    // Read input from text file
    ClassLoader classLoader = FirstPipeline.class.getClassLoader();
    String file = classLoader.getResource("lorem.txt").getFile();
    List<String> lines = Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8);

    // Create empty pipeline
    // Pipeline is a container for both data (PCollection) and operations (PTransforms)
    Pipeline pipeline = Pipeline.create();

    // Transform lines into PCollection
    pipeline
        .apply(Create.of(lines))
        // tokenize the input into words
        .apply(Tokenize.of())
        // count each occurrence of a word
        .apply(Count.perElement())
        // print the contents of PCollection 'result' to standard out
        .apply(PrintElements.of());

    // run the Pipeline
    pipeline.run().waitUntilFinish();
  }
}
