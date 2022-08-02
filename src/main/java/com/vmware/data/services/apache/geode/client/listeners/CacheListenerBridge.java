package com.vmware.data.services.apache.geode.client.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.util.CacheListenerAdapter;

/**
 * 
 * @author Gregory Green
 *
 * @param <K> the region key
 * @param <V> the region value
 */
public class CacheListenerBridge<K, V> extends CacheListenerAdapter<K, V>
{
	/**
	 * Private constructor
	 * @param putConsumer the put consumer to notify
	 */
	private CacheListenerBridge(Consumer<EntryEvent<K, V>> putConsumer, Consumer<EntryEvent<K, V>> removeConsumer)
	{
		super();
		this.putConsumers = new ArrayList<>();
		
		if(putConsumer != null)
			this.putConsumers.add(putConsumer);
		
		this.removeConsumers = new ArrayList<>();
		
		if(removeConsumer != null)
			this.removeConsumers.add(removeConsumer);
	}//------------------------------------------------
	/**
	 * Factory method for put events registration
	 * @param <K> the key type
	 * @param <V> the value type
	 * @param consumer the observer to notify
	 * @return the listener
	 */
	public static<K, V> CacheListenerBridge<K, V> forAfterPut(Consumer<EntryEvent<K, V>> consumer)
	{
		return new CacheListenerBridge<K, V>(consumer,null);
	}//------------------------------------------------
	/**
	 * 	Factory method for delete events registration
	 * @param <K> the key type
	 * @param <V> the value type
	 * @param consumer the observer to notify
	 * @return the listener
	 */
	public static<K, V> CacheListenerBridge<K, V> forAfterDelete(Consumer<EntryEvent<K, V>> consumer)
	{
		return new CacheListenerBridge<K, V>(null,consumer);
	}//------------------------------------------------
	@Override
	public void afterCreate(EntryEvent<K, V> event)
	{
		updatePutConsumer(event);
	}//------------------------------------------------
	@Override
	public void afterUpdate(EntryEvent<K, V> event)
	{
		updatePutConsumer(event);
	}//------------------------------------------------

	@Override
	public void afterDestroy(EntryEvent<K, V> event)
	{
		updateRemoveConsumer(event);
	}//------------------------------------------------
	
	@Override
	public void afterInvalidate(EntryEvent<K, V> event)
	{
		updateRemoveConsumer(event);
	}//------------------------------------------------
	private void updateRemoveConsumer(EntryEvent<K, V> event)
	{
		if(this.removeConsumers == null || this.removeConsumers.isEmpty())
			return;
		
		for (Consumer<EntryEvent<K, V>> removeConsumer : this.removeConsumers)
		{
			removeConsumer.accept(event);
		}
		
	}//------------------------------------------------
	public void addAfterDeleteListener(Consumer<EntryEvent<K, V>> consumer)
	{	
		this.removeConsumers.add(consumer);
	}//------------------------------------------------
	public void addAfterPutListener(Consumer<EntryEvent<K, V>> consumer)
	{	
		this.putConsumers.add(consumer);
	}//------------------------------------------------
	private void updatePutConsumer(EntryEvent<K, V> event)
	{
		if(this.putConsumers == null || this.putConsumers.isEmpty())
			return;
		
		for (Consumer<EntryEvent<K, V>> putConsumer : putConsumers)
		{
			putConsumer.accept(event);
		}
		
	}//------------------------------------------------


	private Collection<Consumer<EntryEvent<K, V>>> putConsumers;
	private  Collection<Consumer<EntryEvent<K, V>>> removeConsumers;
	


}
