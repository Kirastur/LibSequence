package de.polarwolf.libsequence.token;

import java.util.Objects;
import java.util.UUID;

/**
 * Tokens are the libsequences way to authorize some operations.
 *
 */
public final class LibSequenceToken {

	private final UUID token;

	public LibSequenceToken() {
		token = UUID.randomUUID();
	}

	public LibSequenceToken(UUID token) {
		this.token = token;
	}

	@Override
	public int hashCode() {
		return Objects.hash(token);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		LibSequenceToken other = (LibSequenceToken) obj;
		return token.equals(other.token);
	}

	@Override
	public String toString() {
		return token.toString();
	}

	public static LibSequenceToken fromString(String s) {
		UUID token = UUID.fromString(s);
		return new LibSequenceToken(token);
	}

}
