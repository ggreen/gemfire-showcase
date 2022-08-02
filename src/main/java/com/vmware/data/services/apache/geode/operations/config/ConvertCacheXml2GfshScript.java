package com.vmware.data.services.apache.geode.operations.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import nyla.solutions.core.xml.XML;

public class ConvertCacheXml2GfshScript
{
	
	/**
	 * GFSH_CMD_END = ";\n"
	 */
	public static final String GFSH_CMD_END = ";\n";

	public static final void main(String[] args)
	{
		if(args == null || args.length == 0)
			throw new IllegalArgumentException("Argument with cache.xml file required");
		
		try
		{
			 
			File file = Paths.get(args[0]).toFile();
			
			Document doc = XML.toDocument(file);
			
			NodeList pdxList = XML.searchNodesXPath("//pdx", doc);
			
			StringBuilder gfsh =  new StringBuilder(250);
			
			//Add Disk stores
			
			gfsh.append("create disk-store --name=PDX_DISKSTORE --dir=. --max-oplog-size=64").append(GFSH_CMD_END);
			
			gfsh.append("create disk-store --name=REF_DISKSTORE --dir=. --max-oplog-size=128").append(GFSH_CMD_END);
			
			gfsh.append("create disk-store --name=DATA_DISKSTORE --dir=. --max-oplog-size=512").append(GFSH_CMD_END);

			
			//Add PDX
			if(pdxList != null && pdxList.getLength() > 0)
			{
				Node pdxNode = pdxList.item(0);
				String classes = XML.searchNodesXPath("//parameter[@name='classes']/string", pdxNode).item(0).getTextContent();
				gfsh.append("configure pdx --read-serialized=true --portable-auto-serializable-classes=\"").append(classes).append("\" --disk-store=PDX_DS").append(GFSH_CMD_END);
			}
			
			//Process Regions
			NodeList regions = doc.getElementsByTagName("region");
		
			
			for (int i=0; i < regions.getLength();i++)
			{
				Node regionNode = regions.item(i);
				
				String regionName = regionNode.getAttributes().getNamedItem("name").getNodeValue();
				String type = XML.findAttrByRegExp("(refid)|(data-policy)", regionNode);
				
				if(type == null)
					continue;
				
				type = type.toUpperCase(Locale.US);
			
				
				gfsh.append("create region  --name=")
				.append(regionName).append(" --type=").append(type);
				
				//check for colocated-with
				//String colocateWith = XML.findAttr("colocated-with", regionNode);
				String colocateWith = XML.findAttrByRegExp("colocated-with", regionNode);
				if(colocateWith != null)
					gfsh.append(" --colocated-with=/").append(colocateWith);
				
				
				//cache-listener
				Node cacheListerNode = XML.findElementByName("cache-listener",regionNode);
				if(cacheListerNode != null)
				{
					String className = XML.findElementByName("class-name", cacheListerNode).getTextContent();
					gfsh.append(" --cache-listener=").append(className.trim());
				}
				
				
				//--partition-resolver 
				Node partitionResolverNode = XML.findElementByName("partition-resolver", regionNode);
				if(partitionResolverNode != null)
				{
					String className = XML.findElementByName("class-name", partitionResolverNode).getTextContent();
					gfsh.append(" --partition-resolver=").append(className.trim());
					
				}
				
				//add diskstore
				if(type.contains("PERSISTENT"))
				{
					if(type.contains("REPLICATE"))
					{
						gfsh.append("  --disk-store=REF_DISKSTORE");
					}
					else if(type.contains("LOCAL"))
					{
						gfsh.append("  --disk-store=LOCAL_DISKSTORE");
					}
					else if(type.contains("LOCAL"))
					{
						gfsh.append("  --disk-store=DATA_DISKSTORE");
					}
				}
				
				gfsh.append(";\n");
				
				//index
				Collection<Node> indexNodes = XML.findElementsByName("index", regionNode);
				if(indexNodes != null && !indexNodes.isEmpty())
				{
					
					for (Node indexNode : indexNodes)
					{
						gfsh.append("create index --region=/").append(regionName)
						.append(" --name=").append(XML.findAttrByRegExp("name", indexNode))
						.append(" --expression=").append(regionName).append(".").append(XML.findAttrByRegExp("expression", indexNode)).append(";\n");
					
					}	
				}
				
			}
			
			System.out.println(gfsh);
			
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		
	}
}
