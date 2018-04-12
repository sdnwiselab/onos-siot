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

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onlab.osgi.DefaultServiceDirectory;
import org.onosproject.net.Device;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;
import org.onosproject.net.device.DeviceService;
import org.slf4j.Logger;
import org.onlab.packet.IpAddress;
import static org.slf4j.LoggerFactory.getLogger;

@Component(immediate = true)
public class InnerDeviceListener implements DeviceListener {

    private final Logger log = getLogger(getClass());

   // @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)

    protected DeviceService deviceService= DefaultServiceDirectory.getService(DeviceService.class);
    protected Device device;



    @Override
    public void event(DeviceEvent event) {
        /*log.info("New device event.");*/

        switch (event.type()) {
            case DEVICE_ADDED:
                device = event.subject();

            case DEVICE_AVAILABILITY_CHANGED:
                if (deviceService.isAvailable(event.subject().id())) {
                    log.info("Handler Device connected {}", event.subject().id());

                }
                break;
            /* TODO other cases
            case DEVICE_UPDATED:
			case DEVICE_REMOVED:
			case DEVICE_SUSPENDED:
			case PORT_ADDED:
			case PORT_UPDATED:
			case PORT_REMOVED:
			default:
			break;*/
        }
    }

}



