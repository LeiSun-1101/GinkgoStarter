package com.ginkgo.data.configs;

import com.ginkgo.data.DataProperties;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import java.time.Duration;
import java.util.Objects;

@Configuration
@ConditionalOnProperty(prefix = "ginkgo.data.redis", name = "host-name")
public class RedisConfiguration {

    private DataProperties dataProperties;

    @Autowired
    RedisConfiguration(DataProperties dataProperties) {
        this.dataProperties = dataProperties;
    }

    @Bean
    public RedisConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration serverConfiguration = new RedisStandaloneConfiguration();
        serverConfiguration.setHostName(dataProperties.getRedis().getHostName());
        serverConfiguration.setPort(dataProperties.getRedis().getPort());

        if (!Objects.isNull(dataProperties.getRedis().getPassword())) {
            serverConfiguration.setPassword(dataProperties.getRedis().getPassword());
        }

        GenericObjectPoolConfig<Object> pool = new GenericObjectPoolConfig<>();
        pool.setMinIdle(0);
        pool.setMaxIdle(10);
        pool.setMaxTotal(20);
        pool.setMaxWaitMillis(1000);

        LettucePoolingClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder()
                .poolConfig(pool).commandTimeout(Duration.ofMillis(1000)).build();

        return new LettuceConnectionFactory(serverConfiguration, clientConfiguration);
    }
}
