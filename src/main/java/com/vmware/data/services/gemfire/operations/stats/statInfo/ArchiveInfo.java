package com.vmware.data.services.gemfire.operations.stats.statInfo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import com.vmware.data.services.gemfire.operations.stats.GfStatsReader;

import com.vmware.data.services.gemfire.operations.stats.visitors.StatsVisitor;

/**
 * Describes some global information about the archive.
 */
public class ArchiveInfo implements StatsInfo
{
	 private final GfStatsReader archive;
	    private final byte archiveVersion;
	    private final long startTimeStamp; // in milliseconds
	    private final long systemStartTimeStamp; // in milliseconds
	    private final int timeZoneOffset;
	    private final String timeZoneName;
	    private final String systemDirectory;
	    private final long systemId;
	    private final String productVersion;
	    private final String os;
	    private final String machine;

	    public ArchiveInfo(GfStatsReader archive, byte archiveVersion,
	        long startTimeStamp, long systemStartTimeStamp,
	        int timeZoneOffset, String timeZoneName,
	        String systemDirectory, long systemId,
	        String productVersion, String os, String machine) {
	      this.archive = archive;
	      this.archiveVersion = archiveVersion;
	      this.startTimeStamp = startTimeStamp;
	      this.systemStartTimeStamp = systemStartTimeStamp;
	      this.timeZoneOffset = timeZoneOffset;
	      this.timeZoneName = timeZoneName;
	      this.systemDirectory = systemDirectory;
	      this.systemId = systemId;
	      this.productVersion = productVersion;
	      this.os = os;
	      this.machine = machine;
	      archive.setTimeZone(getTimeZone());
	    }


	    public long getStartTimeMillis() {
	      return this.startTimeStamp;
	    }

	    /**
	     * @return the difference, measured in milliseconds, between the time the
	     * archived system was started and midnight, January 1, 1970 UTC.
	     */
	    public long getSystemStartTimeMillis() {
	      return this.systemStartTimeStamp;
	    }

	    /**
	     * @return a numeric id of the archived system.  It can be used in
	     * conjunction with the {@link #getSystemStartTimeMillis} to uniquely
	     * identify an archived system.
	     */
	    public long getSystemId() {
	      return this.systemId;
	    }

	    /**
	     * @return a string describing the operating system the archive was written
	     * on.
	     */
	    public String getOs() {
	      return this.os;
	    }

	    /**
	     * @return a string describing the machine the archive was written on.
	     */
	    public String getMachine() {
	      return this.machine;
	    }

	    /**
	     * @return  the time zone used when the archive was created. This can be
	     * used to print timestamps in the same time zone that was in effect when
	     * the archive was created.
	     */
	    public TimeZone getTimeZone() {
	      TimeZone result = TimeZone.getTimeZone(this.timeZoneName);
	      if (result.getRawOffset() != this.timeZoneOffset) {
	        result = new SimpleTimeZone(this.timeZoneOffset, this.timeZoneName);
	      }
	      return result;
	    }

	    /**
	     * @return a string containing the version of the product that wrote this
	     * archive.
	     */
	    public String getProductVersion() {
	      return this.productVersion;
	    }

	    /**
	     * @return a numeric code that represents the format version used to encode
	     * the archive as a stream of bytes.
	     */
	    public int getArchiveFormatVersion() {
	      return this.archiveVersion;
	    }

	    /**
	     * @return a string describing the system that this archive recorded.
	     */
	    public String getSystem() {
	      return this.systemDirectory;
	    }

	    /**
	     * @return a string representation of this object.
	     */
	    @Override
	    public String toString() {
	      StringWriter sw = new StringWriter();
	      this.dump(new PrintWriter(sw));
	      return sw.toString();
	    }

	    public void dump(PrintWriter stream) {
	      stream.println("archiveVersion=" + archiveVersion);
	      if (archive != null) {
	        stream.println("startDate=" + archive.formatTimeMillis(startTimeStamp));
	      }
	      // stream.println("startTimeStamp=" + startTimeStamp +" tz=" + timeZoneName + " tzOffset=" + timeZoneOffset);
	      // stream.println("timeZone=" + getTimeZone().getDisplayName());
	      stream.println("systemDirectory=" + systemDirectory);
	      
	      if(archive  != null)
	      {
		      stream.println(
			          "systemStartDate=" + archive.formatTimeMillis(systemStartTimeStamp));
			      stream.println("systemId=" + systemId);	    	  
	      }

	      stream.println("productVersion=" + productVersion);
	      stream.println("osInfo=" + os);
	      stream.println("machineInfo=" + machine);
	    }

		@Override
		public void accept(StatsVisitor visitor)
		{
			visitor.visitArchInfo(this);
		}
}
