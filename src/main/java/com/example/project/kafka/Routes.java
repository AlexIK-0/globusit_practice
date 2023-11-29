// Copyright 2023 Kozlov Alexey

package com.example.project.kafka;

import com.example.project.config.KafkaConfig;
import com.example.project.model.Student;
import com.example.project.service.StudentService;
import com.example.project.xsd.generated.com.example.requests.Request;
import com.example.project.xsd.generated.com.example.response.Response;
import com.example.project.xsd.generated.com.example.status.Status;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.UnmarshalException;
import java.sql.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Routes extends RouteBuilder {

    private final StudentService studentService;

    private final KafkaConfig kafkaConfig;

    private AtomicInteger countMessage;

    private AtomicInteger countSuccessMessage;

    private AtomicInteger countErrorMessage;

    @Autowired
    public Routes(StudentService studentService, KafkaConfig kafkaConfig, MeterRegistry meterRegistry) {
        this.studentService = studentService;
        this.kafkaConfig = kafkaConfig;
        countMessage = new AtomicInteger(0);
        countSuccessMessage = new AtomicInteger(0);
        countErrorMessage = new AtomicInteger(0);
        meterRegistry.gauge("countMessage", countMessage);
        meterRegistry.gauge("countSuccessMessage", countSuccessMessage);
        meterRegistry.gauge("countErrorMessage", countErrorMessage);
    }

    @Override
    public void configure() {
        final JaxbDataFormat jaxb = new JaxbDataFormat(Request.class.getPackage().getName());
        try {

            onException(UnmarshalException.class)
            .handled(true)
            .setHeader("Status", simple("ERROR"))
            .setBody(simple("Error unmarshal"))
            .to("direct:status");

            //Request from Kafka
            from("kafka:" + kafkaConfig.getRequestTopic() + "?brokers=" + kafkaConfig.getBootstrapServers() +"&groupId=" + kafkaConfig.getGroupId())
                    .routeId("request from kafka")
                    .to("micrometer:timer:events.timer?action=start")
                    .unmarshal(jaxb)
                    .choice()
                    .when(body().isInstanceOf(Request.class))
                    .to("direct:request")
                    .otherwise()
                    .setBody(simple("Error type"))
                    .setHeader("Status", simple("ERROR"))
                    .to("direct:status");

            //Save to DataBase
            from("direct:request")
                    .routeId("Save to db")
                    .setHeader("Status", simple("SUCCESS"))
                    .process(exchange -> {
                        Request request = exchange.getIn().getBody(Request.class);
                        Student student = new Student(
                                request.getIdst(),
                                request.getFn(),
                                request.getLn(),
                                new Date(request.getDob().toGregorianCalendar().getTime().getTime()),
                                request.getMentor()
                        );
                        studentService.createStudent(student);
                        countMessage.incrementAndGet();
                    })
                    .to("direct:process");

            //Save to Kafka
            from("direct:process")
                    .routeId("Save to kafka")
                    .setHeader("Status", simple("SUCCESS"))
                    .process(exchange -> {
                        Request request = exchange.getIn().getBody(Request.class);

                        Response response = new Response();
                        response.setId(request.getIdst());
                        response.setFn(request.getFn());
                        response.setLn(request.getLn());

                        exchange.getMessage().setBody(response);
                    })
                    .marshal()
                    .json(JsonLibrary.Jackson)
                    .setHeader(KafkaConstants.KEY, simple("response"))
                    .to("kafka:" + kafkaConfig.getResultTopic() + "?brokers=" + kafkaConfig.getBootstrapServers() +"&groupId=" + kafkaConfig.getGroupId())
                    .to("direct:status");


            //Save status
            JaxbDataFormat jaxbStatus = new JaxbDataFormat(Status.class.getPackage().getName());
            from("direct:status")
                    .process(exchange -> {
                        Status status = new Status();
                        if(exchange.getIn().getHeader("Status", String.class).equals("SUCCESS")) {
                            status.setCode(0);
                            countSuccessMessage.incrementAndGet();
                        } else {
                            status.setCode(-1);
                            countErrorMessage.incrementAndGet();
                        }
                        status.setStatus(exchange.getIn().getHeader("Status", String.class));
                        exchange.getMessage().setBody(status);
                    })
                    .marshal(jaxbStatus)
                    .setHeader(KafkaConstants.KEY, simple("response"))
                    .to("kafka:" + kafkaConfig.getStatusTopic() + "?brokers=" + kafkaConfig.getBootstrapServers() +"&groupId=" + kafkaConfig.getGroupId())
                    .to("micrometer:timer:events.timer?action=stop");
        } catch (RuntimeException exception) {
            System.out.println(exception);
        }
    }
}
