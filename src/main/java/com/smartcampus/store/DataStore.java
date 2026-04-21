package com.smartcampus.store;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

public class DataStore {

    // Simulating database tables using thread-safe maps
    public static final Map<String, Room> rooms = new ConcurrentHashMap<>();
    public static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    public static final Map<String, List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();

    // A static block to pre-load some sample data so you can test it easily
    static {
        Room r1 = new Room("LIB-301", "Library Quiet Study", 50);
        rooms.put(r1.getId(), r1);

        Sensor s1 = new Sensor("TEMP-001", "Temperature", "ACTIVE", "LIB-301");
        s1.setCurrentValue(22.5);
        sensors.put(s1.getId(), s1);

        // Add the sensor to the room's list
        r1.getSensorIds().add(s1.getId());
    }
}