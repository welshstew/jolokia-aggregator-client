/**
 *  Copyright 2005-2016 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package com.nullendpoint.aggregator.client;

import com.nullendpoint.aggregator.client.processor.JolokiaClient;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * A spring-boot application that includes a Camel route builder to setup the Camel routes
 */
@ComponentScan
@Configuration
@SpringBootApplication
@ImportResource({"classpath:spring/camel-context.xml"})
public class Application extends RouteBuilder {

    @Value("${jolokia.aggregator.rest.api}")
    public String aggregatorRestApi;

    @Value("${jolokia.aggregator.queue.name}")
    public String queueName;

    // must have a main method spring-boot can run
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void configure() throws Exception {
        from("timer://foo?period=5000").routeId("queueSizeCheck")
            .setBody().constant("Hello World")
            .process("jolokiaClient").setBody().jsonpath("$.[0].value.*.QueueSize")
             .log("my.queue message count: ${body}")
             .choice()
                .when(simple("${body} > 15"))
                    .to("controlbus:route?routeId=queueConsumer&action=start&async=true").endChoice()
                .when(simple("${body} == 0"))
                    .to("controlbus:route?routeId=queueConsumer&action=stop&async=true").endChoice();

        from("timer://foo?period=10000").routeId("queueProducer")
                .setBody(simple("hello")).to("activemq:queue:" + queueName);

        from("activemq:queue:my.queue").routeId("queueConsumer").autoStartup(false)
                .log("Consumed ${body} from " + queueName);

    }
}
