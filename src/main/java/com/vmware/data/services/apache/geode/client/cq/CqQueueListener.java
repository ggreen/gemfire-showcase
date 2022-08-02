package com.vmware.data.services.apache.geode.client.cq;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.geode.cache.Operation;
import org.apache.geode.cache.query.CqEvent;
import org.apache.geode.cache.query.CqListener;
import org.apache.geode.cache.query.CqQuery;
import nyla.solutions.core.patterns.Disposable;
import nyla.solutions.core.util.Debugger;

public class CqQueueListener<E> extends LinkedBlockingQueue<E> 
implements CqListener, Disposable
{	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1260014730371989800L;
	
	@SuppressWarnings("unchecked")
	public void onEvent(CqEvent cqEvent)
	  {
		//get the type of operation being performed
	    Operation operation = cqEvent.getBaseOperation();

	    if (!operation.isCreate() &&
	    		!operation.isUpdate() && 
	    		!operation.isPutAll())
	    			return; //Ignore some operations (ex: non WRITEs) 
	    
	    // key and new value from the event
	    E entry = (E)cqEvent.getNewValue();
	    
	    this.add(entry);
	  }//------------------------------------------------
	 
	  public void onError(CqEvent cqEvent)
	  {
		  Debugger.println(this,"ERROR"+cqEvent);
	  }//------------------------------------------------
	  @Override
	  public void close()
	  {
		  if (cqQuery != null)
			{
				try { cqQuery.close(); } catch (Exception e){Debugger.println(e.getMessage());}
			}
	  }//------------------------------------------------

	/**
	 * @param cqQuery the cqQuery to set
	 */
	public void setCqQuery(CqQuery cqQuery)
	{
		this.cqQuery = cqQuery;
	}

	@Override
	public void dispose()
	{
		this.close();
	}//------------------------------------------------
	private transient CqQuery cqQuery = null;
	 
}
