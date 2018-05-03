package biodiv;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.redis.hibernate52.SingletonRedisRegionFactory;
import org.hibernate.cache.redis.util.RedisCacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiodivRedisRegionFactory extends SingletonRedisRegionFactory {

	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(BiodivRedisRegionFactory.class);

	private final AtomicInteger referenceCount = new AtomicInteger();

	public BiodivRedisRegionFactory(Properties props) {
		super(props);
	}

	@Override
	public void start(SessionFactoryOptions options, Properties properties) {

		log.debug("BiodivRedisRegionFactory is starting... options={}, properties={}", options, properties);

		this.options = options;
		try {
			if (redis == null) {
				properties.put(RedisCacheUtil.REDISSON_CONFIG, PropertyHelper.getRedisconConfig());
				RedisCacheUtil.loadCacheProperties(properties);
				this.redis = createRedisClient();
				this.cacheTimestamper = createCacheTimestamper(redis, BiodivRedisRegionFactory.class.getName());
			}
			if (redis != null) {
				referenceCount.incrementAndGet();
				log.info("BiodivRedisRegionFactory started at {}", System.currentTimeMillis());
			}
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}

	private static class PropertyHelper {

		private static String getRedisconConfig() {
			String propsName = "redisson.yaml";
			return PropertyHelper.class.getClassLoader().getResource(propsName).getPath();
		}
	}
}
