package util;

import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;

import java.io.IOException;


public class MyAndroidHttpTransport extends HttpTransportSE {

	private int mTimeOut = 10000; // 默认超时时间�?10s

	public MyAndroidHttpTransport(String pUrl) {
		super(pUrl);
	}

	public MyAndroidHttpTransport(String pUrl, int pTimeOut) {
		super(pUrl);
		this.mTimeOut = pTimeOut;
	}

	protected ServiceConnection getServiceConnection(String pUrl)
			throws IOException {
		ServiceConnectionSE serviceConnection = new ServiceConnectionSE(pUrl);
		serviceConnection.setConnectionTimeOut(mTimeOut);
		return serviceConnection;
	}
}