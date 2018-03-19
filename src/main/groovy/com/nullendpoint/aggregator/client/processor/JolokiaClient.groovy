package com.nullendpoint.aggregator.client.processor

import groovy.json.JsonOutput
import groovyx.net.http.HTTPBuilder
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component(value="jolokiaClient")
class JolokiaClient implements Processor{

    @Value('${jolokia.aggregator.rest.api}')
    public String aggregatorRestApi // = "http://jolokia-aggregator-myproject.192.168.42.48.nip.io"

    @Value('${jolokia.aggregator.rest.token}')
    public String token

    @Value('${jolokia.aggregator.queue.name}')
    public String queueName

    Logger log = LoggerFactory.getLogger(this.getClass())

    @Override
    void process(Exchange exchange) throws Exception {

        if(token == ""){
            token = new File('/var/run/secrets/kubernetes.io/serviceaccount/token').toString()
        }

        def http = new HTTPBuilder(aggregatorRestApi)
        http.ignoreSSLIssues()

        /**
         * curl -X POST \
         http://jolokia-aggregator-myproject.192.168.42.48.nip.io/jolokia/aggregate \
         -H 'Accept: application/json' \
         -H 'Authorization: Bearer p7MAxL2gRQD8wRwWti9dq97eV8UNhWG2xWGcKwG7GyI' \
         -H 'Cache-Control: no-cache' \
         -H 'Content-Type: application/json' \
         -H 'Postman-Token: 6c1c83fe-d0af-9f1b-edb4-080bfeede7ea' \
         -H 'kube-label: application=broker' \
         -H 'kube-namespace: myproject' \
         -d '{"type":"read","mbean":"org.apache.activemq:type=Broker,brokerName=kube-lookup,destinationType=*,destinationName=", "attribute": ["QueueSize","ConsumerCount"]}'
         */

        http.request( groovyx.net.http.Method.POST, groovyx.net.http.ContentType.JSON) {

            uri.path = '/jolokia/aggregate'
            body =  JsonOutput.toJson([type: 'read', mbean: "org.apache.activemq:type=Broker,brokerName=kube-lookup,destinationType=*,destinationName=$queueName", attribute:["QueueSize"]])
            headers = [Authorization: "Bearer ${token}",
                       Accept: groovyx.net.http.ContentType.JSON,
                       'kube-namespace':'myproject',
                       'kube-label':'application=broker']

            log.debug("Authorization: Bearer ${token}")

            response.success = { resp, json ->
                log.debug("POST response status: ${resp.statusLine}")
                assert resp.statusLine.statusCode == 200
                log.debug( "Got response: ${resp.statusLine}")
                log.debug( "Content-Type: ${resp.headers.'Content-Type'}")
                //make it a list here, just so it is easier later...
                exchange.in.body = JsonOutput.toJson(json)
                log.debug("response: ${json}")
            }


            response.'404' = {
                println 'Not found'
                log.error("unable to call jolokia")
            }

            response.'403' = { resp, json ->
                log.error("unauthorized to call jolokia")
                log.debug("POST response status: ${resp.statusLine}")
                log.debug( "Got response: ${resp.statusLine}")
                log.debug( "Content-Type: ${resp.headers.'Content-Type'}")
                exchange.in.body = json
                log.debug("response: ${json}")
            }

            response.failure = { resp ->
                log.debug(resp.data)
            }
        }

    }
}
