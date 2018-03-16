package util;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import java.util.Map;

public class XmlParserUtil {

	private static final String NAMESPACE = "http://tempuri.org/";
	public XmlParserUtil( ) {
	}
	public SoapObject getSoapObject(String METHOD_NAME, String[] key,
			Map<String, Object> value) {
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		if (key != null && value != null)
			for (int i = 0; i < key.length; i++) {
				request.addProperty(key[i], value.get(key[i]));
			}
		envelope.bodyOut = request;
		envelope.setOutputSoapObject(request);envelope.bodyOut = request;
		int timeout = 10000; // settimeout 10s
		MyAndroidHttpTransport ht = new MyAndroidHttpTransport(ConstantUtil.url, timeout);
		ht.debug = true;
		SoapObject result = null;		
		try {
			ht.call(ConstantUtil.url+ "/" + METHOD_NAME, envelope);
			result = (SoapObject) envelope.getResponse();
			result = (SoapObject) result.getProperty(0);
			
		} catch (Exception ex) {
			return null;
		}
		return result;
	}
}