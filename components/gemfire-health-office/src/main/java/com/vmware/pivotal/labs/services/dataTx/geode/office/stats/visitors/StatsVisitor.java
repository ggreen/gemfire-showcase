package com.vmware.pivotal.labs.services.dataTx.geode.office.stats.visitors;


import com.vmware.pivotal.labs.services.dataTx.geode.office.stats.statInfo.*;

public interface StatsVisitor
{

	default  void visitArchInfo(ArchiveInfo archiveInfo){}
	
	default void visitResourceType(ResourceType resourceType){}
	
	default  void visitTimeStampSeries(TimeStampSeries timeStampSeries){}
	default void visitResourceInsts(ResourceInst[] resourceInsts){}
	default void visitResourceInst(ResourceInst resourceInst){}
	default void visitSimpleValue(SimpleValue simpleValue) {}
}
