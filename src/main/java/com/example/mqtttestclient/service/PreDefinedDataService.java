package com.example.mqtttestclient.service;

import com.example.mqtttestclient.entity.*;
import com.example.mqtttestclient.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PreDefinedDataService {
    @Autowired
    private SensorRepository sensorRepository;
    @Autowired
    private DataTypeRepository dataRepository;
    @Autowired
    private FactoryRepository factoryRepository;
    @Autowired
    private PlantRepository plantRepository;
    @Autowired
    private MachineRepository machineRepository;

    public void addSensor(Long id, String name){
        Sensor sensor = new Sensor();
        sensor.setName(name);
        sensor.setId(id);

        try {
            sensorRepository.save(sensor);
        }catch (Exception e){
            return;// ResponseEntity.badRequest().body(e.getCause());
        }

        //return;// ResponseEntity.badRequest().body("Good Request");
    }

    public void addDataType(Long id, String name){
        DataType dataType = new DataType();
        dataType.setId(id);
        dataType.setName(name);

        dataRepository.save(dataType);
    }

    public void addFactory(Long id, String name){
        Factory factory = new Factory();
        factory.setId(id);
        factory.setName(name);

        factoryRepository.save(factory);
    }

    public void addPlant(Long id, String name){
        Plant plant = new Plant();
        plant.setId(id);
        plant.setName(name);

        plantRepository.save(plant);
    }

    public void addMachine(Long id, String name){
        Machine machine = new Machine();
        machine.setId(id);
        machine.setName(name);

        machineRepository.save(machine);
    }

    public void sensorSaveAll(){
        //Sensor
        addSensor(1L , "Temperature Sensor");
        addSensor(2L , "Pressure Sensor");
        addSensor(3L , "Inductive Proximity Sensor");
        addSensor(4L , "Capacitive Proximity Sensor");
        addSensor(5L , "Photoelectric Sensor");
        addSensor(6L , "Position Sensor");
        addSensor(7L , "IR Sensor");
        addSensor(8L , "Load Cell");

        //Data Type
        addDataType(1L, "Boolean");
        addDataType(2L, "char");
        addDataType(3L, "uint8_t");
        addDataType(4L, "Uint16_t");
        addDataType(5L, "Uint32_t");
        addDataType(6L, "int8_t");
        addDataType(7L, "int16_t");
        addDataType(8L, "int32_t");
        addDataType(9L, "float");
        addDataType(10L, "double");
        addDataType(11L, "long");

        //Factory
        addFactory(1L, "A1");
        addFactory(2L, "ACSL");
        addFactory(3L, "ACL");
        addFactory(4L, "AIL");

        //Plant
        for(Long i = Long.valueOf(1); i <= 200; i++){
            addPlant(i, "Plant "+i);
        }

        //Machine
        for(Long i = Long.valueOf(1); i <= 20; i++){
            addMachine(i, "Machine "+i);
        }

    }
}
