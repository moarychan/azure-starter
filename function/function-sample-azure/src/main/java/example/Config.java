/*
 * Copyright 2012-2019 the original author or authors.
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

package example;

import java.util.Arrays;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import com.microsoft.azure.functions.ExecutionContext;

@SpringBootApplication
public class Config {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext run = SpringApplication.run(Config.class, args);
		String[] beans = run.getBeanDefinitionNames();

		Arrays.sort(beans);
		for (String bean : beans)
		{
			System.out.println(bean + " of Type :: " + run.getBean(bean).getClass());
		}
	}

//	ExecutionContext context
	@Bean
	public Function<String, String> uppercase(ExecutionContext context) {
		return value -> {
			context.getLogger().info("Uppercasing " + value);
			return value.toUpperCase();
		};
	}

}

