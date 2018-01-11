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
import org.onosproject.net.Host;
import org.onosproject.net.host.HostEvent;
import org.onosproject.net.host.HostListener;
import org.onosproject.net.host.HostService;
import org.slf4j.Logger;
import org.onlab.packet.IpAddress;

import static org.slf4j.LoggerFactory.getLogger;

@Component(immediate = true)
public class InnerHostListener implements HostListener {

    private final Logger log = getLogger(getClass());
    protected Host host;
    protected CreateChannel canale = new CreateChannel();

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected HostService hostService;


    @Override
    public void event(HostEvent event) {
        log.info("New host event.");

        switch (event.type()) {
            case HOST_ADDED:
                host = event.subject();
                try {
                    canale.createChannel(StatoRete.cookie, host.id().toString(), host.mac().toString(), host.ipAddresses());
                    log.info("New host: " + host.id().toString() + " added and channel created.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            case HOST_REMOVED:
                break;
        }
    }
}



