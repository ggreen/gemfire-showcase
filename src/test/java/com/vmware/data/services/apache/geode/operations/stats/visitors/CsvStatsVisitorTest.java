package com.vmware.data.services.apache.geode.operations.stats.visitors;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.vmware.data.services.apache.geode.operations.stats.GfStatsReader;
import com.vmware.data.services.apache.geode.operations.stats.visitors.CsvStatsVisitor;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.ResourceInst;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.ResourceType;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.StatDescriptor;
import com.vmware.data.services.apache.geode.operations.stats.statInfo.StatValue;
import nyla.solutions.core.io.IO;
import org.junit.jupiter.api.*;

import static org.mockito.Mockito.*;

public class CsvStatsVisitorTest
{

	@Test
	void test_from_archive_appends_csv()
	throws IOException
	{
		File statsFile = new File("target/archive/server2.gfs");
		IO.mkdir(statsFile.getParentFile());

		File sourceStatsFile = new File("src/test/resources/stats/server2.gfs");
		IO.copy(sourceStatsFile,
				statsFile.getParent());
		IO.ops(statsFile).deleteDirectoryFiles();

		CsvStatsVisitor subject = new CsvStatsVisitor(statsFile);
		File actual = subject.toFile(statsFile,null);
		assertEquals(statsFile.getName()+".csv",actual.getName());
	}

	@Test
	public void testGenericCsvStatsVisitorFile()
	{
		String resourceTypeName = "junitResourceType";
		String resourceInstName = "junitResourceInstName";
		
		String expectFileName =  "target/stats/server1.gfs-junitResourceInstName-junitResourceType.csv";
		
		if(Paths.get(expectFileName).toFile().delete())
		{
			System.out.println("Old csvFile deleleted");
		}

		Path outputPath = Paths.get(expectFileName).getParent();
		outputPath.toFile().mkdirs();

		File csvFile = Paths.get("target/stats").toFile();

		csvFile.mkdirs();
		
		CsvStatsVisitor subject = new CsvStatsVisitor(csvFile);
		
		StatValue stat1 = mock(StatValue.class);
		StatDescriptor stat1Descriptor = mock(StatDescriptor.class);
		when(stat1Descriptor.getName()).thenReturn("stat1");
		when(stat1.getSnapshotsMaximum()).thenReturn(Double.valueOf(10));
		when(stat1.getDescriptor()).thenReturn(stat1Descriptor);
		
		StatValue stat2 = mock(StatValue.class);

		StatDescriptor stat2Descriptor = mock(StatDescriptor.class);
		when(stat2Descriptor.getName()).thenReturn("stat2");
		
		when(stat2.getSnapshotsMaximum()).thenReturn(Double.valueOf(20));
		when(stat2.getDescriptor()).thenReturn(stat2Descriptor);

		ResourceType resourceType = mock(ResourceType.class);
		when(resourceType.getName()).thenReturn(resourceTypeName);
	
		subject.visitResourceType(resourceType);
		
		ResourceInst resourceInst = mock(ResourceInst.class);
		GfStatsReader mockArchive = mock(GfStatsReader.class);
		when(mockArchive.getArchiveFile()).thenReturn(new File("server1.gfs"));
		when(resourceInst.getArchive()).thenReturn(mockArchive);
		when(resourceInst.getName()).thenReturn(resourceInstName);
		when(resourceInst.getType()).thenReturn(resourceType);
		when(resourceInst.getStatValue("stat1")).thenReturn(stat1);
		when(resourceInst.getStatValue("stat2")).thenReturn(stat2);
		
		when(resourceType.getStat("stat1")).thenReturn(stat1Descriptor);
		when(resourceType.getStat("stat2")).thenReturn(stat2Descriptor);
		
		StatValue[] statValues = { stat1,stat2};

		when(resourceInst.getStatValues()).thenReturn(statValues);
		subject.visitResourceInst(resourceInst);
		
		assertTrue(Paths.get(expectFileName).toFile().exists());
	}


	@Test
	void toFile_dir()
	{
		ResourceInst mockResourceInst = mock(ResourceInst.class);
		ResourceType mockResourceType = mock(ResourceType.class);
		GfStatsReader mockArchive = mock(GfStatsReader.class);

		String expectedName = "Hello";
		String expectedType = "Type";
		String expectedArchiveName = "server.gfs";

		when(mockResourceInst.getName()).thenReturn(expectedName);
		when(mockResourceInst.getType()).thenReturn(mockResourceType);
		when(mockResourceType.getName()).thenReturn(expectedType);
		when(mockResourceInst.getArchive()).thenReturn(mockArchive);
		when(mockArchive.getArchiveFile()).thenReturn(new File(expectedArchiveName));


		Path expectedPath1 = Paths.get("target/csv/1");
		expectedPath1.toFile().mkdirs();

		CsvStatsVisitor subject = new CsvStatsVisitor(
				expectedPath1.toFile());

		File file = subject.toFile(expectedPath1.toFile(),mockResourceInst);
		assertNotNull(file);

		assertEquals("server.gfs-Hello-Type.csv",file.getName());

	}

	@Test
	void toFile_file()
	{
		ResourceInst mockResourceInst = mock(ResourceInst.class);
		ResourceType mockResourceType = mock(ResourceType.class);
		GfStatsReader mockArchive = mock(GfStatsReader.class);

		String expectedName = "Hello";
		String expectedType = "Type";
		String expectedArchiveName = "server.gfs";

		when(mockResourceInst.getName()).thenReturn(expectedName);
		when(mockResourceInst.getType()).thenReturn(mockResourceType);
		when(mockResourceType.getName()).thenReturn(expectedType);
		when(mockResourceInst.getArchive()).thenReturn(mockArchive);
		when(mockArchive.getArchiveFileName()).thenReturn(expectedArchiveName);


		Path expectedPath1 = Paths.get("target/csv/file.csv");
		expectedPath1.getParent().toFile().mkdirs();

		CsvStatsVisitor subject = new CsvStatsVisitor(
				expectedPath1.toFile());

		File file = subject.toFile(expectedPath1.toFile(),mockResourceInst);
		assertNotNull(file);

		assertEquals("file.csv",file.getName());

	}


	@Test
	public void test_setoutputfile_for_multiple_stats()
			throws Exception
	{
		Path expectedPath1 = Paths.get("target/csv/1");
		Path expectedPath2 = Paths.get("target/csv/2");

		expectedPath1.toFile().mkdirs();
		IO.ops(expectedPath1.toFile()).deleteDirectoryFiles();
		expectedPath2.toFile().mkdirs();
		IO.ops(expectedPath2.toFile()).deleteDirectoryFiles();

		File statFile = new File("src/test/resources/stats/server1.gfs");
		GfStatsReader statsReader = new GfStatsReader(statFile);


		CsvStatsVisitor v1 = new CsvStatsVisitor(
				expectedPath1.toFile());
		CsvStatsVisitor v2 = new CsvStatsVisitor(
				expectedPath2.toFile());

		statsReader.acceptVisitors(v1, v2);

		assertTrue(IO.listFiles(expectedPath1.toFile(),"*.csv").length > 0);
		assertTrue(IO.listFiles(expectedPath2.toFile(),"*.csv").length > 0);


	}
}
