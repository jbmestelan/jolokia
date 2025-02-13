package org.jolokia.service.jmx.handler;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.management.*;
import javax.management.openmbean.CompositeData;

import org.jolokia.server.core.request.*;
import org.jolokia.server.core.service.serializer.Serializer;
import org.jolokia.service.serializer.JolokiaSerializer;
import org.jolokia.server.core.util.TestJolokiaContext;
import org.testng.annotations.*;

import static org.jolokia.server.core.util.RequestType.EXEC;
import static org.testng.Assert.*;

/*
 * Copyright 2009-2011 Roland Huss
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

public class OpenExecHandlerTest {
    private ExecHandler handler;

    private ObjectName oName;

    @BeforeMethod
    public void createHandler() {
        TestJolokiaContext ctx = new TestJolokiaContext.Builder().services(Serializer.class, new JolokiaSerializer()).build();
        handler = new ExecHandler();
        handler.init(ctx, null);
    }

    @BeforeClass
    public void registerMBean() throws MalformedObjectNameException, MBeanException, InstanceAlreadyExistsException, IOException, NotCompliantMBeanException, ReflectionException {
        oName = new ObjectName("jolokia:test=openExec");

        MBeanServerConnection conn = getMBeanServer();
        conn.createMBean(OpenExecData.class.getName(),oName);
    }

    @AfterClass
    public void unregisterMBean() throws InstanceNotFoundException, MBeanRegistrationException, IOException {
        MBeanServerConnection conn = getMBeanServer();
        conn.unregisterMBean(oName);
    }

    /**
     * If a field in the argument is not set it will be set to its default value
     */
    @Test
    public void missingField() throws InstanceNotFoundException, IOException, ReflectionException, AttributeNotFoundException, MBeanException, MalformedObjectNameException, NotChangedException, EmptyResponseException {
    	// set a value just for stringField, leave out intField
        JolokiaExecRequest request = new JolokiaRequestBuilder(EXEC, oName).
                operation("compositeData").
                arguments("{ \"stringField\":\"aString\" }").
                build();
        CompositeData data  = (CompositeData) handler.handleSingleServerRequest(getMBeanServer(), request);
        assertEquals(data.get("stringField"),"aString");
        assertNull(data.get("map"));
        assertEquals(data.get("intField"), 0);
    }

    /**
     * set a non-existing field
     */
    @Test(expectedExceptions={IllegalArgumentException.class})
    public void invalidField() throws MalformedObjectNameException, InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException, IOException, NotChangedException, EmptyResponseException {
    	// set a value just for stringField, leave out intField
        JolokiaExecRequest request = new JolokiaRequestBuilder(EXEC, oName).
                operation("compositeData").
                arguments("{ \"nonExistentField\":\"aString\" }").
                build();
        handler.handleSingleServerRequest(getMBeanServer(), request);
    }

    /**
     * Give an invalid value (wrong type) for the field
     */
    @Test(expectedExceptions={ NumberFormatException.class })
    public void invalidValueForField() throws MalformedObjectNameException, InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException, IOException, NotChangedException, EmptyResponseException {
        JolokiaExecRequest request = new JolokiaRequestBuilder(EXEC, oName).
        		operation("compositeData").
        		arguments("{ \"intField\":\"aString\" }").
        		build();
        handler.handleSingleServerRequest(getMBeanServer(), request);
    }

    /**
     * The operation argument is an array
     */
    @Test
    public void arrayOfComposites() throws InstanceNotFoundException, IOException, ReflectionException, AttributeNotFoundException, MBeanException, MalformedObjectNameException, NotChangedException, EmptyResponseException {
        JolokiaExecRequest request = new JolokiaRequestBuilder(EXEC, oName).
                operation("arrayData").
                arguments("[ { \"stringField\":\"aString\" } ]").
                build();
        handler.handleSingleServerRequest(getMBeanServer(), request);
    }

    /**
     * The operation argument is a List
     */
    @Test
    public void listOfComposites() throws InstanceNotFoundException, IOException, ReflectionException, AttributeNotFoundException, MBeanException, MalformedObjectNameException, NotChangedException, EmptyResponseException {
        JolokiaExecRequest request = new JolokiaRequestBuilder(EXEC, oName).
                operation("listData").
                arguments("[ { \"stringField\":\"aString\" } ]").
                build();
        handler.handleSingleServerRequest(getMBeanServer(), request);
    }

    /**
     * The operation argument is a Set
     */
    @Test
    public void setOfComposites() throws InstanceNotFoundException, IOException, ReflectionException, AttributeNotFoundException, MBeanException, MalformedObjectNameException, NotChangedException, EmptyResponseException {
        JolokiaExecRequest request = new JolokiaRequestBuilder(EXEC, oName).
                operation("opSetData").
                arguments("[ { \"stringField\":\"aString\" } ]").
                build();
        handler.handleSingleServerRequest(getMBeanServer(), request);
    }

    /**
     * The operation argument is a Map
     */
    @Test
    public void mapOfComposites() throws InstanceNotFoundException, IOException, ReflectionException, AttributeNotFoundException, MBeanException, MalformedObjectNameException, NotChangedException, EmptyResponseException {
        JolokiaExecRequest request = new JolokiaRequestBuilder(EXEC, oName).
                operation("mapData").
                arguments("{ \"aKey\":{ \"stringField\":\"aString\" } }").
                build();
        handler.handleSingleServerRequest(getMBeanServer(), request);
    }

    /**
     * Set a nested field inside the composite argument
     */
    @Test
    public void nested() throws MalformedObjectNameException, InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException, IOException, NotChangedException, EmptyResponseException {
        JolokiaExecRequest request = new JolokiaRequestBuilder(EXEC, oName).
				operation("compositeData").
				arguments("{ \"nestedClass\":{\"nestedField\":\"aString\"} }").
				build();
        handler.handleSingleServerRequest(getMBeanServer(), request);
    }

    /**
     * Set an array field inside the composite argument
     */
    @Test
    public void compositeWithArrayField() throws MalformedObjectNameException, InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException, IOException, NotChangedException, EmptyResponseException {
        JolokiaExecRequest request = new JolokiaRequestBuilder(EXEC, oName).
        		operation("compositeData").
        		arguments("{ \"array\":[\"one\", \"two\"] }").
        		build();
        handler.handleSingleServerRequest(getMBeanServer(), request);
    }

    /**
     * Set a List field inside the composite argument with values that can't be converted to
     * the list element type
     */
    @Test(expectedExceptions={NumberFormatException.class})
    public void invalidTypeCompositeWithListField() throws MalformedObjectNameException, InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException, IOException, NotChangedException, EmptyResponseException {
        JolokiaExecRequest request = new JolokiaRequestBuilder(EXEC, oName).
        		operation("compositeData").
        		arguments("{ \"list\":[\"one\", \"two\"] }").
        		build();
        handler.handleSingleServerRequest(getMBeanServer(), request);
    }

    /**
     * Set a List field inside the composite argument
     */
    @Test
    public void compositeWithListField() throws MalformedObjectNameException, InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException, IOException, NotChangedException, EmptyResponseException {
        JolokiaExecRequest request = new JolokiaRequestBuilder(EXEC, oName).
        		operation("compositeData").
        		arguments("{ \"list\":[\"1\", \"2\"] }").
        		build();
        handler.handleSingleServerRequest(getMBeanServer(), request);
    }

    /**
     * Set a Map field inside the composite argument
     */
    @Test
    public void compositeWithMapField() throws MalformedObjectNameException, InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException, IOException, NotChangedException, EmptyResponseException {
        JolokiaExecRequest request = new JolokiaRequestBuilder(EXEC, oName).
        		operation("compositeData").
        		arguments("{ \"map\":{ \"5\":{\"nestedField\":\"value1\"}, \"7\":{\"nestedField\":\"value2\"} } }").
        		build();
        handler.handleSingleServerRequest(getMBeanServer(), request);
    }

    /**
     * Set a Set field inside the composite argument
     */
    @Test
    public void compositeWithSetField() throws MalformedObjectNameException, InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException, IOException, NotChangedException, EmptyResponseException {
        JolokiaExecRequest request = new JolokiaRequestBuilder(EXEC, oName).
        		operation("compositeData").
        		arguments("{ \"set\": [\"value1\",\"value2\"] }").
        		build();
        handler.handleSingleServerRequest(getMBeanServer(), request);
    }


    @Test
    public void overloaded() throws MalformedObjectNameException, InstanceNotFoundException, AttributeNotFoundException, ReflectionException, MBeanException, IOException, NotChangedException, EmptyResponseException {
        JolokiaExecRequest request = new JolokiaRequestBuilder(EXEC, oName).
				operation("overloaded(javax.management.openmbean.CompositeData)").
				arguments("{ \"stringField\": \"aString\" }").
				build();
        handler.handleSingleServerRequest(getMBeanServer(), request);
    }


    private MBeanServerConnection getMBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }

}
