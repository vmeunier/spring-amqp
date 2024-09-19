/*
 * Copyright 2022-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.amqp.rabbit.support.micrometer;

import io.micrometer.common.KeyValues;
import io.micrometer.common.docs.KeyName;
import io.micrometer.observation.Observation.Context;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.docs.ObservationDocumentation;

/**
 * Spring Rabbit Observation for listeners.
 * @author Gary Russell
 * @author Vincent Meunier
 * @since 3.0
 */
public enum RabbitListenerObservation implements ObservationDocumentation {

	/**
	 * Observation for Rabbit listeners.
	 */
	LISTENER_OBSERVATION {
		@Override
		public Class<? extends ObservationConvention<? extends Context>> getDefaultConvention() {
			return DefaultRabbitListenerObservationConvention.class;
		}

		@Override
		public String getPrefix() {
			return "spring.rabbit.listener";
		}

		@Override
		public KeyName[] getLowCardinalityKeyNames() {
			return ListenerLowCardinalityTags.values();
		}

	};

	/**
	 * Low cardinality tags.
	 */
	public enum ListenerLowCardinalityTags implements KeyName {

		/**
		 * Listener id.
		 */
		LISTENER_ID {
			@Override
			public String asString() {
				return "spring.rabbit.listener.id";
			}

		},

		/**
		 * The exchange the listener is plugged to (empty if default exchange)
		 * @since 3.2
		 */
		EXCHANGE {
			@Override
			public String asString() {
				return "messaging.destination.name";
			}

		},

		/**
		 * The routing key the listener is plugged to
		 * @since 3.2
		 */
		ROUTING_KEY {
			@Override
			public String asString() {
				return "messaging.rabbitmq.destination.routing_key";
			}

		}
	}

	/**
	 * Default {@link RabbitListenerObservationConvention} for Rabbit listener key values.
	 */
	public static class DefaultRabbitListenerObservationConvention implements RabbitListenerObservationConvention {

		/**
		 * A singleton instance of the convention.
		 */
		public static final DefaultRabbitListenerObservationConvention INSTANCE =
				new DefaultRabbitListenerObservationConvention();

		@Override
		public KeyValues getLowCardinalityKeyValues(RabbitMessageReceiverContext context) {
			return KeyValues.of(
					RabbitListenerObservation.ListenerLowCardinalityTags.LISTENER_ID.asString(), context.getListenerId(),
					RabbitListenerObservation.ListenerLowCardinalityTags.EXCHANGE.asString(),
					context.getCarrier().getMessageProperties().getReceivedExchange(),
					RabbitListenerObservation.ListenerLowCardinalityTags.ROUTING_KEY.asString(),
					context.getCarrier().getMessageProperties().getReceivedRoutingKey()
			);
		}

		@Override
		public String getContextualName(RabbitMessageReceiverContext context) {
			return context.getSource() + " receive";
		}

	}

}
