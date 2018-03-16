package util;

import org.ksoap2.serialization.SoapObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class uploadParser {
	private XmlParserUtil xmlParserUtil;
	public uploadParser( )
	{
		xmlParserUtil = new XmlParserUtil();
	}

	public boolean summit(String[] key, Map<String, Object> value, String METHOD_NAME) {
		SoapObject result = null;

			result = xmlParserUtil.getSoapObject(METHOD_NAME, key, value);
			try {
				if (result.hasProperty("Result")) {
					return  Integer.parseInt(result.getProperty("Result").toString())==1 ? true : false;
				}
			}
			catch (Exception e) {
				return false;
			}
			return false;
		}
}

