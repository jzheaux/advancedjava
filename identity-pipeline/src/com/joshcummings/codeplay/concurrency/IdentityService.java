package com.joshcummings.codeplay.concurrency;

import java.util.List;
import java.util.function.Predicate;

public interface IdentityService {
	void persistOrUpdateBestMatch(Identity identity);

	List<Identity> search(Predicate<Identity> pred);
}
