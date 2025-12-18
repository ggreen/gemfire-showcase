package showcase.gemfire.demo.functions.locking.domain;

import org.apache.geode.cache.Cache;

public record LockServiceContext(String lockServiceName, Cache cache) {
}
