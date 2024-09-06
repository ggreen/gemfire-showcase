package com.vmware.pivotal.labs.services.dataTx.geode.office.stats.statInfo;


import com.vmware.pivotal.labs.services.dataTx.geode.office.stats.visitors.StatsVisitor;

public interface StatsInfo
{
	 void accept(StatsVisitor visitor);
}
