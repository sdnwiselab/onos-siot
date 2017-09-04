/*
 * Copyright 2014 Open Networking Laboratory
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
package com.github.sdnwiselab.siot.cli;

import com.github.sdnwiselab.siot.CreateChannelService;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.completer.StringsCompleter;
import org.onosproject.cli.AbstractShellCommand;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;



public class ChannelIdCompleter implements Completer {

    @Override
    public int complete(String buffer, int cursor, List<String> candidates) {
        // Delegate string completer
        String[] cookies=null;
        List<String> ids = new ArrayList<String>();
        StringsCompleter delegate = new StringsCompleter();
        CreateChannelService service = AbstractShellCommand.get(CreateChannelService.class);
        try {
            cookies = service.getCookie();}
        catch (Exception e) {
            e.printStackTrace();}

        try{
            ids = service.getIdChannels(cookies);}
        catch (Exception e) {
            e.printStackTrace();}

        Iterator<String> it = ids.iterator();
        SortedSet<String> strings = delegate.getStrings();
        while (it.hasNext()) {
            strings.add(it.next());
        }

        // Now let the completer do the work for figuring out what to offer.
        return delegate.complete(buffer, cursor, candidates);

    }

}
