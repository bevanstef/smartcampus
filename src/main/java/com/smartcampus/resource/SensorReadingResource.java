package com.smartcampus.resource;

import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.store.DataStore;
import com.smartcampus.exception.SensorUnavailableException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SensorReadingResource {
    private String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadingHistory() {
        List<SensorReading> readings = DataStore.sensorReadings.getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(readings).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        Sensor sensor = DataStore.sensors.get(sensorId);

        // Safety Check: Ensure the sensor exists
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Sensor not found\"}").build();
        }

        // Part 5.3: State Constraint (403 Forbidden)
        // If sensor is in MAINTENANCE, it cannot accept new readings
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                    "Sensor " + sensorId + " is currently in MAINTENANCE and cannot accept readings.");
        }

        // Generate ID and Timestamp if not provided (Best practice)
        if (reading.getId() == null)
            reading.setId(UUID.randomUUID().toString());
        if (reading.getTimestamp() <= 0)
            reading.setTimestamp(System.currentTimeMillis());

        // Save the reading to history
        DataStore.sensorReadings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);

        // MANDATORY SIDE EFFECT: Update parent sensor's current value
        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}