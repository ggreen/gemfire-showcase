package io.pivotal.dataTx.geode.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.geode.pdx.PdxReader;
import org.apache.geode.pdx.PdxSerializable;
import org.apache.geode.pdx.PdxWriter;

public class User implements PdxSerializable, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7849491509886229835L;

	private String userName;
	private byte[] encryptedPassword;
	private Collection<String> priviledges;


	public User()
	{
	}

	public User(String userName, byte[] password, Collection<String> priviledges)
	{
		this.userName = userName;
		this.encryptedPassword = password;
		this.priviledges = priviledges;
	}
	/**
	 * @return the userName
	 */
	public String getUserName()
	{
		return userName;
	}
	/**
	 * @return the password
	 */
	public byte[] getEncryptedPassword()
	{
		return encryptedPassword;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	/**
	 * @param password the password to set
	 */
	public void setEncryptedPassword(byte[] password)
	{
		this.encryptedPassword = password;
	}
	
	/**
	 * @return the priviledges
	 */
	public Collection<String> getPriviledges()
	{
		if(this.priviledges == null || this.priviledges.isEmpty())
			return null;
		
		return new ArrayList<String>(priviledges);
	}
	/**
	 * @param priviledges the priviledges to set
	 */
	public void setPriviledges(Collection<String> priviledges)
	{
		this.priviledges = priviledges;
	}

	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("User [userName=").append(userName).append(", priviledges=").append(priviledges).append("]");
		return builder.toString();
	}
	@Override
	public void fromData(PdxReader reader)
	{
		userName = reader.readString("userName");
		encryptedPassword = reader.readByteArray("encryptedPassword");
		String[] textArray = reader.readStringArray("priviledges");
		if(textArray != null && textArray.length > 0)
			this.priviledges = Arrays.asList(textArray);
	}
	
	@Override
	public void toData(PdxWriter writer)
	{
		writer.writeString("userName", userName);
		
		writer.writeByteArray("encryptedPassword", this.encryptedPassword);
		
		if(this.priviledges == null)
			writer.writeStringArray("priviledges", null);
		else
		{
			String[] array = new String[priviledges.size()];
			this.priviledges.toArray(array);
			
			writer.writeStringArray("priviledges", array);
		}
	}
}
