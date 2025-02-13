package org.jolokia.service.jmx.handler;

/*
 * Copyright 2009-2013 Roland Huss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Map;

/**
 * @author roland
 * @since 21.04.11
 */
@SuppressWarnings("rawtypes")
public interface WriteDataMBean {

    int getSimple();

    void setSimple(int i);

    Map getMap();

    void setMap(Map m);

    Boolean getBoolean();

    void setBoolean(Boolean b);

    String getReadOnly();
}
