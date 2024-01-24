package com.example.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@SpringBootApplication
public class IntegrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntegrationApplication.class, args);
	}

//	@Bean
//	MessageChannel fromAToB() {
//		return MessageChannels.direct().getObject();
//	}
//
//	@Bean
//	IntegrationFlow publishFlow() {
//		return IntegrationFlow
//				.from((MessageSource<String>) () -> MessageBuilder.withPayload("Time is: " + Instant.now() + "!").build(), poller -> poller.poller(pm -> pm.fixedRate(Duration.ofSeconds(1))))
//				.channel(fromAToB())
//				.get();
//	}
//
//	@Bean
//	IntegrationFlow listenerFlow() {
//		return IntegrationFlow
//				.from(fromAToB())
//				.handle(message -> System.out.println("The payload is: " + message.getPayload()))
//				.get();
//	}


	private static String text() {
		return Math.random() >.5 ?
				"Time is: " + Instant.now() :
				"Updating time";
	}

	private static String time() {
		return "Time is: " + Instant.now();
	}

//	@Bean
//	IntegrationFlow flow() {
//		return IntegrationFlow
//				.from((MessageSource<String>) () -> MessageBuilder.withPayload(text()).build(),
//						poller -> poller.poller(pm -> PollerFactory.fixedRate(Duration.ofSeconds(1))))
////				.filter(String.class, source -> source.contains("Time is"))
////				.transform((GenericTransformer<String, String>) String::toUpperCase)
//				.handle((GenericHandler<String>) (payload, headers) -> payload.contains("Time is") ? payload : null)
//				.handle((GenericHandler<String>) (payload, headers) -> payload.toUpperCase())
//				.handle(message -> System.out.println("The payload is: " + message.getPayload()))
//				.get();
//		//handle and transform have the same logic, but different implementation
//		// behind the scene, filter & transformation is written in terms of message handlers
//		// they are just more expressive
//	}

	@Component
	static class CustomMessageSource implements MessageSource<String> {

		@Override
		public Message<String> receive() {
			return MessageBuilder.withPayload(time()).build();
		}
	}

	@Bean
	IntegrationFlow flow(CustomMessageSource customMessageSource) {
		return IntegrationFlow
				.from(customMessageSource)
				.handle((GenericHandler<String>) (payload, headers) -> payload.contains("Time is") ? payload : null) // get rid of warnings
				.transform((GenericTransformer<String, String>) String::toUpperCase)
				.handle(message -> System.out.println("The payload is: " + message.getPayload()))
				.get();
	}

}
