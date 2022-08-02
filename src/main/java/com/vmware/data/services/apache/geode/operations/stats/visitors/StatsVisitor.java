package com.vmware.data.services.apache.geode.operations.stats.visitors;

import com.vmware.data.services.apache.geode.operations.stats.statInfo.ResourceInst;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.ArchiveInfo;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.ResourceType;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.SimpleValue;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.TimeStampSeries;

public interface StatsVisitor
{

	default  void visitArchInfo(ArchiveInfo archiveInfo){}
	
	default void visitResourceType(ResourceType resourceType){}
	
	default  void visitTimeStampSeries(TimeStampSeries timeStampSeries){}
	default void visitResourceInsts(ResourceInst[] resourceInsts){}
	default void visitResourceInst(ResourceInst resourceInst){}
	default void visitSimpleValue(SimpleValue simpleValue) {}
}
