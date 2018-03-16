package util;

import org.ksoap2.serialization.SoapObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class findListParser {
	private XmlParserUtil xmlParserUtil;
	public findListParser( )
	{
		xmlParserUtil = new XmlParserUtil();
	}
	@SuppressWarnings({ "rawtypes", "static-access" })
	public List findforlist(Class clazz, String METHOD_NAME, String[] key,
			Map<String, Object> value, String xmlChildName) {
		List<Object> Fieldlist = new ArrayList<Object>();
		Object obj = null;
		Field[] fields = clazz.getDeclaredFields();
		SoapObject result = null;
		try {
			result = xmlParserUtil.getSoapObject(METHOD_NAME, key, value);
			if (!result.hasProperty(xmlChildName)) {
				return null;
			}		
			for (int i = 0; i < result.getPropertyCount(); i++) {
				SoapObject soapChilds = result;
				if (soapChilds.getProperty(i).toString().indexOf("anyType") > -1)
					soapChilds = (SoapObject) result.getProperty(i);
				obj = clazz.newInstance();
				for (int j = 0; j < fields.length; j++) {
					Field f = fields[j];
					boolean flag = f.isAccessible();
					f.setAccessible(true);
					try {
						if (soapChilds.getProperty(f.getName().toString())
								.toString() != null) {
							if (soapChilds.getProperty(f.getName().toString())
									.toString().equals("anyType{}"))
								f.set(obj, "");
							else
								f.set(obj, soapChilds.getProperty(f.getName())
										.toString());
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					f.setAccessible(flag);
				}
				Fieldlist.add(obj);
			}
		} catch (Exception ex) {
			Fieldlist = null;
		}
		return Fieldlist;
	}
}

