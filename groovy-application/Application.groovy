/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.cloudfoundry.java.test.controller.ApplicationController
import org.cloudfoundry.java.test.controller.DataSourceController
import org.cloudfoundry.java.test.controller.MongoDbController
import org.cloudfoundry.java.test.controller.RabbitController
import org.cloudfoundry.java.test.controller.RedisController
import org.cloudfoundry.java.test.core.DataSourceUtils
import org.cloudfoundry.java.test.core.FakeMongoDbFactory
import org.cloudfoundry.java.test.core.FakeRedisConnectionFactory
import org.cloudfoundry.java.test.core.HealthUtils
import org.cloudfoundry.java.test.core.InitializationUtils
import org.cloudfoundry.java.test.core.MemoryUtils
import org.cloudfoundry.java.test.core.MongoDbUtils
import org.cloudfoundry.java.test.core.RabbitUtils
import org.cloudfoundry.java.test.core.RedisUtils
import org.cloudfoundry.java.test.core.RuntimeUtils
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassRelativeResourceLoader
import org.springframework.data.mongodb.MongoDbFactory
import org.springframework.data.redis.connection.RedisConnectionFactory

import javax.sql.DataSource

@EnableAutoConfiguration
public class Application {

    static void main(String[] args) {
        new InitializationUtils().fail()
        new MemoryUtils().outOfMemoryOnStart()

        args += '--server.port=' + System.env['PORT']
        new SpringApplication(new ClassRelativeResourceLoader(Application.class), Application.class).run(args)
    }

    @Bean
    static DataSourceUtils dataSourceUtils() {
        return new DataSourceUtils();
    }

    @Bean
    static HealthUtils healthUtils() {
        return new HealthUtils();
    }

    @Bean
    static MemoryUtils memoryUtils() {
        def memory = new MemoryUtils()
        memory.outOfMemoryOnStart()

        return memory
    }

    @Bean
    static RedisUtils redisUtils() {
        return new RedisUtils()
    }

    @Bean
    static MongoDbUtils mongoDbUtils() {
        return new MongoDbUtils()
    }

    @Bean
    static RabbitUtils rabbitUtils() {
        return new RabbitUtils()
    }

    @Bean
    static RuntimeUtils runtimeUtils() {
        return new RuntimeUtils()
    }

    @Bean
    static RedisConnectionFactory redisConnectionFactory() {
        return new FakeRedisConnectionFactory()
    }

    @Bean
    static MongoDbFactory mongoDbFactory() {
        return new FakeMongoDbFactory()
    }

    @Bean
    static ConnectionFactory rabbitConnectionFactory() {
        return new CachingConnectionFactory(null, 0)
    }

    @Bean
    static ApplicationController applicationController() {
        return new ApplicationController(healthUtils(), memoryUtils(), runtimeUtils())
    }

    @Bean
    static DataSourceController dataSourceController(DataSource dataSource) {
        return new DataSourceController(dataSourceUtils(), dataSource)
    }

    @Bean
    static RedisController redisController(RedisConnectionFactory redisConnectionFactory) {
        return new RedisController(redisUtils(), redisConnectionFactory)
    }

    @Bean
    static MongoDbController mongoDbController(MongoDbFactory mongoDbFactory) {
        return new MongoDbController(mongoDbUtils(), mongoDbFactory)
    }

    @Bean
    static RabbitController rabbitController(ConnectionFactory rabbitConnectionFactory) {
        return new RabbitController(rabbitUtils(), rabbitConnectionFactory)
    }
}
