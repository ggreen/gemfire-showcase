package io.pivotal.dataTx.geode.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nyla.solutions.core.util.Organizer;
import org.apache.geode.pdx.PdxReader;
import org.apache.geode.pdx.PdxWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserTest
{
	private User subject = new User();
	private String userName = "user";
	private byte[] encryptedPassword = "encryptedPassword".getBytes(StandardCharsets.UTF_8);
	private Collection<String> priviledges = List.of("p1","p2");

	@Mock
	private PdxReader reader;

	@Mock
	private PdxWriter writer;

	@Test
	public void testToStringDoesNotHavePassword()
	{

		byte[] passwords = {1,23,23};
		
		subject.setEncryptedPassword(passwords);
		
		assertTrue(!subject.toString().contains("encryptedPassword"));
	}

	@Test
	void userName() {
		String expected = "user";
		subject.setUserName(expected);

		assertEquals(expected,subject.getUserName());
	}

	@Test
	void encryptedPassword() {

		byte[] expected = "encryptedPassword".getBytes(StandardCharsets.UTF_8);
		subject.setEncryptedPassword(expected);

		assertEquals(expected,subject.getEncryptedPassword());
	}

	@Test
	void privileges() {
		Collection<String> expected = List.of("test");
		subject.setPriviledges(expected);

		assertEquals(expected,subject.getPriviledges());
	}

	@Test
	void fromData() {
		when(reader.readString(anyString()))
				.thenReturn(userName);
		when(reader.readByteArray(anyString()))
				.thenReturn(encryptedPassword);

		when(reader.readStringArray(anyString()))
				.thenReturn(Organizer.toArrayString(priviledges));

		subject.fromData(reader);

		assertEquals(subject.getUserName(), userName);
		assertEquals(subject.getEncryptedPassword(), encryptedPassword);
		assertEquals(subject.getPriviledges(), priviledges);

	}

	@Test
	void
	toData() {

		subject.setUserName(userName);
		subject.setPriviledges(priviledges);
		subject.setEncryptedPassword(encryptedPassword);

		subject.toData(writer);

		verify(writer).writeString(anyString(),anyString());
		verify(writer).writeByteArray(anyString(),any());
		verify(writer).writeStringArray(anyString(),any());
	}
}
