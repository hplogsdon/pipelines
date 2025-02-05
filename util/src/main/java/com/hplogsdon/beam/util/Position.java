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

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.Optional;
import lombok.Value;

@Value
public class Position {
  public static final long EARTH_DIAMETER = 6_371_000;
  public static final double METER_TO_ANGLE = 180 / (EARTH_DIAMETER * Math.PI);

  double latitude;
  double longitude;
  long timestamp;

  public static Optional<Position> parseFrom(String tsv) {
    String[] parts = tsv.split("\t");
    if (parts.length < 3) {
      return Optional.empty();
    }
    if (parts.length > 3) {
      parts = Arrays.copyOfRange(parts, parts.length - 3, parts.length);
    }
    return Optional.of(
        new Position(
            Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Long.parseLong(parts[2])));
  }

  public static Position random(long stamp) {
    return new Position((Math.random() - 0.5) * 180, (Math.random() - 0.5) * 180, stamp);
  }

  public double distance(Position other) {
    return calculateDistanceOfPositions(this, other);
  }

  public Position move(
      double latitudeDirection,
      double longitudeDirection,
      double speedMeterPerSec,
      long timeMillis) {
    double desiredStepSize = (speedMeterPerSec * timeMillis) / 1000 * METER_TO_ANGLE;
    double deltaSize =
        Math.sqrt(latitudeDirection * latitudeDirection + longitudeDirection * longitudeDirection);
    Preconditions.checkArgument(deltaSize > 0);
    return new Position(
        getLatitude() + latitudeDirection / deltaSize * desiredStepSize,
        getLongitude() + longitudeDirection / deltaSize * desiredStepSize,
        getTimestamp() + timeMillis);
  }

  private static double calculateDistanceOfPositions(Position a, Position b) {
    double deltaLatitude = (a.getLatitude() - b.getLatitude()) * Math.PI / 180;
    double deltaLongitude = (a.getLongitude() - b.getLongitude()) * Math.PI / 180;
    double latitudeIncInMeters = calculateDelta(deltaLatitude);
    double longitudeIncInMeters = calculateDelta(deltaLongitude);

    return EARTH_DIAMETER
        * Math.sqrt(
            latitudeIncInMeters * latitudeIncInMeters
                + longitudeIncInMeters * longitudeIncInMeters);
  }

  private static double calculateDelta(double deltaLatOrLon) {
    return Math.sqrt(2 * (1 - Math.cos(deltaLatOrLon)));
  }
}
