package com.hplogsdon.beam.chapter1;

import com.hplogsdon.beam.util.Tokenize;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.testing.TestStream;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TimestampedValue;
import org.joda.time.Instant;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MissingWindowPipeline {
    public static void main(String[] args) throws IOException {
        ClassLoader loader = MissingWindowPipeline.class.getClassLoader();
        String file = loader.getResource("lorem.txt").getFile();
        List<String> lines = Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8);

        // create pipeline
        Pipeline pipeline = Pipeline.create();

        // create a 'TestStream'
        // utility that emulates an unbounded data sources
        TestStream.Builder<String> streamBuilder = TestStream.create(StringUtf8Coder.of());

        // need a timestamp for each element
        Instant now = Instant.now();

        List<TimestampedValue<String>> timestamped =
                IntStream.range(0, lines.size())
                        .mapToObj(i -> TimestampedValue.of(lines.get(i), now.plus(i)))
                        .collect(Collectors.toList());

        for (TimestampedValue<String> value : timestamped) {
            streamBuilder = streamBuilder.addElements(value);
        }

        // create the unbounded PCollection from TestStream
        PCollection<String> input = pipeline.apply(streamBuilder.advanceWatermarkToInfinity());

        PCollection<String> words = input.apply(Tokenize.of());
        words.apply(Count.perElement());
    }
}
