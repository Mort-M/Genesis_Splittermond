package org.prelle.splittermond.chargen.jfx.dialogs;

import java.text.Normalizer;
import java.util.Locale;

import org.prelle.splimo.MastershipOrSpecialization;

/** Search matching shared by the mastership and specialization lists. */
final class MastershipSearchFilter {

	private MastershipSearchFilter() {
	}

	static boolean matches(MastershipOrSpecialization entry, String normalizedFilter) {
		if (normalizedFilter==null || normalizedFilter.isEmpty())
			return true;
		return contains(entry.getName(), normalizedFilter);
	}

	private static boolean contains(String text, String normalizedFilter) {
		return text!=null && normalize(text).contains(normalizedFilter);
	}

	static String normalize(String text) {
		if (text==null)
			return "";
		String normalized = Normalizer.normalize(text.trim(), Normalizer.Form.NFD);
		return normalized.replaceAll("\\p{M}+", "").toLowerCase(Locale.GERMAN);
	}
}
