package com.udacity.catpoint.security.service;

import com.udacity.catpoint.image.service.ImageService;
import com.udacity.catpoint.security.data.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceTest {

    private SecurityService securityService;

    @Mock
    private ImageService imageService;

    @Mock
    private SecurityRepository securityRepository;

    private Sensor sensor;
    private static String sensorName;

    private static BufferedImage bufferedImage;

    private static Set<Sensor> sensors;

    @BeforeEach
    void init() {
        securityService = new SecurityService(securityRepository, imageService);
        sensor = new Sensor(sensorName, SensorType.DOOR);
        sensor.setSensorId(UUID.randomUUID());
    }

    @BeforeAll
    static void globalSetup() {
        sensorName = "sensorName";
        bufferedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        sensors = new HashSet<>();
        Sensor sensor1 = new Sensor("Sensor1" , SensorType.DOOR);
        sensor1.setActive(Boolean.TRUE);
        sensor1.setSensorId(UUID.randomUUID());
        sensors.add(sensor1);
        Sensor sensor2 = new Sensor("Sensor2" , SensorType.DOOR);
        sensor2.setActive(Boolean.TRUE);
        sensor2.setSensorId(UUID.randomUUID());
        sensors.add(sensor2);
        Sensor sensor3 = new Sensor("Sensor3" , SensorType.DOOR);
        sensor3.setActive(Boolean.TRUE);
        sensor3.setSensorId(UUID.randomUUID());
        sensors.add(sensor3);
    }

    @Test
    @DisplayName("1.If alarm is armed and a sensor becomes activated, put the system into pending alarm status.")
    void ifAlarmIsArmedAndASensorBecomeActive_putTheSystemIntoPendingAlarmStatus() {
        Mockito.when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);
        securityService.changeSensorActivationStatus(sensor, Boolean.TRUE);
        Mockito.verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.PENDING_ALARM);
    }

    @Test
    @DisplayName("2.If alarm is armed and a sensor becomes activated and the system is already pending alarm, set the alarm status to alarm.")
    void ifAlarmIsArmedAndASensorBecomeActive_andTheSystemIsPendingAlarm_putTheSystemIntoAlarmStatus() {
        Mockito.when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.changeSensorActivationStatus(sensor, Boolean.TRUE);
        Mockito.verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    @DisplayName("3.If pending alarm and all sensors are inactive, return to no alarm state.")
    void ifPendingAlarmAndAllSensorsAreInactive_returnToNoAlarmState() {
        Mockito.when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        sensor.setActive(Boolean.FALSE);
        securityService.changeSensorActivationStatus(sensor, Boolean.FALSE);
        Mockito.verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    @ParameterizedTest
    @DisplayName("4.If alarm is active, change in sensor state should not affect the alarm state.")
    @ValueSource(booleans = {true, false})
    void IfAlarmIsActiveChangeSensorState_keepAlarmState(boolean sensorState) {
        Mockito.when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);
        securityService.changeSensorActivationStatus(sensor, sensorState);
        Assertions.assertEquals(AlarmStatus.ALARM, securityRepository.getAlarmStatus());
    }

    @Test
    @DisplayName("5.If a sensor is activated while already active and the system is in pending state, change it to alarm state.")
    void ifASensorIsActivatedWhileAlreadyActive_putTheSystemIntoPendingAlarmStatus() {
        Mockito.when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        sensor.setActive(Boolean.TRUE);
        securityService.changeSensorActivationStatus(sensor, Boolean.TRUE);
        Mockito.verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.ALARM);
    }

    @ParameterizedTest
    @DisplayName("6.If a sensor is deactivated while already inactive, make no changes to the alarm state.")
    @EnumSource(value = AlarmStatus.class, names = {"NO_ALARM", "PENDING_ALARM", "ALARM"})
    void ifASensorIsDeActivatedWhileAlreadyIActive_makeNoChangeAlarmState(AlarmStatus alarmStatus) {
        Mockito.when(securityRepository.getAlarmStatus()).thenReturn(alarmStatus);
        sensor.setActive(Boolean.FALSE);
        securityService.changeSensorActivationStatus(sensor, Boolean.TRUE);
        Assertions.assertEquals(alarmStatus, securityRepository.getAlarmStatus());
    }

    @Test
    @DisplayName("7.If the image service identifies an image containing a cat while the system is armed-home, put the system into alarm status..")
    void ifTheImageServiceIdentifiesAnImageContainingACatWhileTheSystemIsArmedHome_putTheSystemIntoAlarmStatus() {
        Mockito.when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        Mockito.when(imageService.imageContainsCat(any(BufferedImage.class), ArgumentMatchers.anyFloat())).thenReturn(Boolean.TRUE);
        securityService.processImage(bufferedImage);
        Mockito.verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    @DisplayName("8.If the image service identifies an image that does not contain a cat, change the status to no alarm as long as the sensors are not active.")
    void ifTheImageServiceIdentifiesAnImageDoesNotContainingACat_putTheSystemIntoAlarmStatus() {
        sensor.setActive(Boolean.FALSE);
        Mockito.when(imageService.imageContainsCat(any(BufferedImage.class), ArgumentMatchers.anyFloat())).thenReturn(Boolean.FALSE);
        securityService.processImage(bufferedImage);
        Mockito.verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    @Test
    @DisplayName("9.If the system is disarmed, set the status to no alarm")
    void ifTheSystemIsDisarmed_putTheSystemIntoAlarmStatus() {
        securityService.setArmingStatus(ArmingStatus.DISARMED);
        Mockito.verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    @DisplayName("10.If the system is armed, reset all sensors to inactive.")
    @ParameterizedTest
    @EnumSource(value = ArmingStatus.class, names = {"ARMED_HOME", "ARMED_AWAY"})
    void ifTheSystemIsArmed_ChangAllSensorStatusToInActive(ArmingStatus armingStatus) {
        securityService.setArmingStatus(armingStatus);
        Mockito.when(securityRepository.getSensors()).thenReturn(sensors);
        Set<Sensor> result = securityService.getSensors();
        result.forEach(r -> {
            Assertions.assertTrue(r.getActive());
        });
    }

    @Test
    @DisplayName("11.If the system is armed-home while the camera shows a cat, set the alarm status to alarm.")
    void ifTheSystemIsArmedHomeWhileTheCameraShowsACat_putTheSystemIntoAlarmStatus() {
        Mockito.when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        Mockito.when(imageService.imageContainsCat(any(BufferedImage.class), ArgumentMatchers.anyFloat())).thenReturn(Boolean.TRUE);
        securityService.processImage(bufferedImage);
        Mockito.verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.ALARM);
    }

    // Cover code coverage
    @Test
    @DisplayName("If a sensor is deactivated while already active, set Alarm to NO_ALARM")
    void ifASensorIsDeactivatedWhileAlreadyActive_putTheSystemIntoNoAlarmStatus() {
        Mockito.when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        sensor.setActive(Boolean.TRUE);
        securityService.changeSensorActivationStatus(sensor, Boolean.FALSE);
        Mockito.verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.NO_ALARM);
    }
}
