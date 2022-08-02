package com.vmware.data.services.apache.geode.operations.stats.statInfo;

import com.vmware.data.services.apache.geode.operations.stats.visitors.StatsVisitor;

public interface StatsInfo
{
	 void accept(StatsVisitor visitor);
}
