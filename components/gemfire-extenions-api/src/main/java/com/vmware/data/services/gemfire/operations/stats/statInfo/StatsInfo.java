package com.vmware.data.services.gemfire.operations.stats.statInfo;

import com.vmware.data.services.gemfire.operations.stats.visitors.StatsVisitor;

public interface StatsInfo
{
	 void accept(StatsVisitor visitor);
}
