package es.academia.campushub.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;


@Configuration // Indica q es configuracion
@EnableCaching //Indica q el sistema tiene Cache
public class CacheConfig  {

    @Bean //Registra la configuracion como componente de Sprin
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(60)) //Indica la duracion que se queda en memoria
                .disableCachingNullValues() //Evita valores en null
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(RedisSerializer.json())); //Guarda los datos en JSON
    }
}
