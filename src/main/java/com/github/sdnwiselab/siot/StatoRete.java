/*
 * Copyright 2017-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.sdnwiselab.siot;

import org.apache.felix.scr.annotations.*;
import org.onosproject.net.Device;
import org.onosproject.net.Host;
import org.onosproject.net.Port;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.host.HostService;
import org.slf4j.Logger;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;


@Component(immediate = true)
public class StatoRete {

    private final Logger log = getLogger(getClass());
    protected InnerDeviceListener deviceListener;
    protected InnerHostListener hostListener;
    protected CreateChannel canale;
    protected static String[] cookie;


    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected HostService hostService;

    @Activate
    protected void activate() {
        //Channel cookie
        this.canale = new CreateChannel();
        CookieHandler.setDefault(new CookieManager());
        String[] cookielogin = null;

        try {
            cookielogin = canale.getCookie();
            StatoRete.cookie = cookielogin;
            canale.sendPost(cookielogin);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            canale.getListChannels(cookielogin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //adding listeners
        this.hostListener = new InnerHostListener();
        this.deviceListener = new InnerDeviceListener();

        deviceService.addListener(deviceListener);
        hostService.addListener(hostListener);
        log.info("Started network reader and Listeners Activated.");


        try {
            for (Host ho : hostService.getHosts()) {
                canale.createChannel(cookielogin, ho.id().toString(), ho.mac().toString());
            }
            for (Device dev : deviceService.getAvailableDevices()) {
                canale.createChannel(cookielogin, dev.id().toString(), " ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        allDevices(deviceService);   //Print the device for manufacturer
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped network reader");
        deviceService.removeListener(deviceListener);
        hostService.removeListener(hostListener);
        log.info("Listener stopped");

    }


    //Generic methode used by others functions.
    public static <E> Collection<E> makeCollection(Iterable<E> iter) {
        Collection<E> list = new ArrayList<E>();
        for (E item : iter) {
            list.add(item);
        }
        return list;
    }

    //Gives all the devices and print the device for constructor
    protected void allDevices(DeviceService deviceService) {
        Iterable<Device> devices = deviceService.getAvailableDevices();
        Collection<Device> devicesList = makeCollection(devices);
        if (devicesList != null && devicesList.size() != 0) {
            log.info("Devices detected: " + Integer.toString(devicesList.size()));
            HashMap mapDevices = devicesHashMap(devicesList);
            System.out.println(groupDeviceByConstructor(mapDevices));


        } else {
            log.error("No device found");
        }

    }


    //Create a hashmap with keys=ids e values the manufacturers of the devices.
    protected HashMap<String, Device> devicesHashMap(Collection<Device> devicesList) {
        HashMap mapDevice = new HashMap();
        for (Device dev : devicesList) {
            mapDevice.put(dev.id(), dev);
        }
        return mapDevice;
    }

    //Create a hashmap with keys: Id and values: MAC.
    protected List<Port> devicesMACHashMap(HashMap<String, Device> mapDevice) {

        Set keys = mapDevice.keySet();
        List<Port> ports = new ArrayList<>();
        List<java.lang.Object> devices = new ArrayList<java.lang.Object>();

        for (Object key : keys) {
            Device device = mapDevice.get(key);
            ports = deviceService.getPorts(device.id());
        }

        return ports;
    }


    //Return a list of all the device with the same manufacturer
    protected List<String> groupDeviceByConstructor(HashMap<String, Device> mapDevice) {

        Set keys = mapDevice.keySet();
        Set<String> costruttori = new HashSet<>();
        List<String> devices = new ArrayList<>();

        for (Object key : keys) {
            Device device = mapDevice.get(key);
            costruttori.add(device.manufacturer());
        }

        for (Iterator<String> it = costruttori.iterator(); it.hasNext(); ) {
            String f = it.next();
            devices.add(f);
            for (Object key : keys) {
                Device device = mapDevice.get(key);
                if (f.equals(device.manufacturer())) {
                    devices.add(device.id().toString());
                }
            }
        }
        return devices;
    }

}





