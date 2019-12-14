package viettel.com.sf.utils.config;

import lombok.NoArgsConstructor;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static viettel.com.sf.utils.StringUtils.escape;
import static viettel.com.sf.utils.StringUtils.unescape;

/**
 * @author duclm
 */
@NoArgsConstructor
public class Ini {

	private static final Pattern sectionPattern;
	private static final Pattern keyValuePattern;
	private static final HashSet<Character> comments;

	static {
		sectionPattern = Pattern.compile("\\s*\\[([^]]*)]\\s*");
		keyValuePattern = Pattern.compile("\\s*([^=]*)=(.*)");
		comments = new HashSet<Character>() {{
			add('#');
			add(';');
		}};
	}

	private final Map<String, Map<String, String>> entries = new LinkedHashMap<>();

	public Ini(String path) throws IOException {
		load(path);
	}

	public Ini(InputStream stream) throws IOException {
		load(stream);
	}

	public static void defineCommentChars(Character... chars) {
		comments.clear();
		comments.addAll(Arrays.asList(chars));
	}

	public void save(String path) throws IOException {
		StringBuilder sb = new StringBuilder();
		entries.forEach((key, value) -> {
			sb.append('[').append(key).append(']').append('\n');
			value.forEach((k, v) -> sb.append(escape(k)).append('=').append(escape(v)).append('\n'));
			sb.append('\n');
		});

		try (OutputStream stream = new FileOutputStream(path)) {
			stream.write(sb.toString().getBytes());
		}
	}

	public void load(String path) throws IOException {
		entries.clear();
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			parse(br);
		}
	}

	public void load(InputStream stream) throws IOException {
		entries.clear();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
			parse(br);
		}
	}

	private void parse(BufferedReader br) throws IOException {
		String line;
		String section = null;
		while ((line = br.readLine()) != null) {
			if (line.length() < 1 || comments.contains(line.trim().charAt(0))) {
				continue;
			}
			Matcher checker = sectionPattern.matcher(line);
			if (checker.matches()) {
				section = checker.group(1).trim();
			} else {
				checker = keyValuePattern.matcher(line);
				if (!checker.matches()) {
					continue;
				}

				String key = unescape(checker.group(1).trim());
				String value = unescape(checker.group(2).trim());
				put(section, key, value);
			}
		}
	}

	public void put(String section, String key, String value) {
		entries.computeIfAbsent(section, sec -> new LinkedHashMap<>()).put(key, value);
	}

	public Optional<String> get(String section, String key) {
		Map<String, String> map = entries.get(section);
		return map == null ? Optional.empty() : Optional.of(map.get(key));
	}

	public String get(String section, String key, String defaultValue) {
		Optional<String> s = get(section, key);
		return s.orElse(defaultValue);
	}

	public int getInt(String section, String key, int defaultValue) {
		Optional<String> s = get(section, key);
		try {
			return s.map(Integer::parseInt).orElse(defaultValue);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public float getFloat(String section, String key, float defaultValue) {
		Optional<String> s = get(section, key);
		try {
			return s.map(Float::parseFloat).orElse(defaultValue);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public double getDouble(String section, String key, double defaultValue) {
		Optional<String> s = get(section, key);
		try {
			return s.map(Double::parseDouble).orElse(defaultValue);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public boolean contains(String key) {
		return entries.containsKey(key);
	}

	public Optional<Map<String, String>> get(String key) {
		return Optional.ofNullable(entries.get(key));
	}
}
