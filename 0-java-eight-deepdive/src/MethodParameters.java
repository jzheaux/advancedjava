import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MethodParameters {
	@Uri(value="/crocodile/{id}")
	public String getCrocodile(String id) {
		return "Croc #" + id;
	}
	
	@Uri(value="/crocodile")
	public List<String> getCrocodiles(String id) {
		return Arrays.asList(getCrocodile("1"), getCrocodile("2"));
	}
	
	public static boolean isDivisible(int n, int k, int d) {
		int num = 1, den = 1;
		for ( int i = n; i >= k; i-- ) {
			num *= i;
			den *= i - (n - k);
		}
		return num / den % d == 0;
	}
	
	public static boolean isAMatch(String uri, Method m) {
		Uri pattern = m.getAnnotation(Uri.class);
		if ( pattern == null ) return false;
		
		String[] patternParts = pattern.value().split("/");
		String[] uriParts = uri.split("/");
		
		if ( patternParts.length == uriParts.length ) {
			for ( int i = 0; i < patternParts.length; i++ ) {
				if ( !patternParts[i].startsWith("{") ) {
					if ( !patternParts[i].equals(uriParts[i]) ) {
						return false;
					}
				}
			}
		} else {
			return false;
		}
		
		return true;
	}
	
	public static Object[] extractParams(String uri, Method m) {
		if ( isAMatch(uri, m) ) {
			Uri pattern = m.getAnnotation(Uri.class);
			String[] patternParts = pattern.value().split("/");
			String[] uriParts = uri.split("/");
			Map<String, String> mapping = new HashMap<>();
			for ( int i = 0; i < patternParts.length; i++ ) {
				String part = patternParts[i];
				if ( part.startsWith("{") ) {
					mapping.put(part.substring(1, part.length() - 1), uriParts[i]);
				}
			}
			Object[] params = new Object[mapping.size()];
			Parameter[] p = m.getParameters();
			for ( int i = 0; i < p.length; i++ ) {
				String name = p[i].getName();
				Class<?> type = p[i].getType();
				params[i] = mapping.get(name);
			}
			return params;
		}
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		String uri = "/crocodile/2";
		MethodParameters mp = new MethodParameters();
		Method[] ms = mp.getClass().getDeclaredMethods();
		for ( Method m : ms ) {
			Object[] params = extractParams(uri, m);
			if ( params != null ) {
				System.out.println(m.invoke(mp, params));
			}
		}
		System.out.println(isDivisible(9,5,6));
	}
}
