package com.awslabs.iot.client.helpers.iot.interfaces;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface WebsocketsHelper {
    MqttCallback buildMessageCallback(Function<String, MqttMessage> messageArrivedHandler);

    MqttClient connectMqttClient() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, MqttException;

    MqttClient connectMqttClientAndSubscribe(String topic) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, MqttException;

    MqttClient connectMqttClientAndPublish(String topic, String message) throws MqttException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException;

    Void subscribe(MqttClient mqttClient, String topic) throws MqttException;

    Void close(MqttClient mqttClient) throws MqttException;

    Void publish(MqttClient mqttClient, String topic, String message) throws MqttException;

    @FunctionalInterface
    public
    interface Function<String, MqttMessage> {
        Void handleMessage(String topic, MqttMessage mqttMessage);
    }
}
