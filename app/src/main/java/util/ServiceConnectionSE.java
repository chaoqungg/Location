package util;


import org.ksoap2.transport.ServiceConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ServiceConnectionSE implements ServiceConnection {
	/**
	 * 在访问远程服务器提供的服务时，有时会因为网络问题或�?�是服务器端问题，�?�导致客户端侧一直处于请求连接状态，
	 * 此时我们希望可以控制请求得不到响应的超时时间TimeOut.
	 * 
	 * 首先重写架包中的ServiceConnectionSE.Java，添加设置超时时间的方法，可以在你的工程里重写这个类
	 * 
	 * **/
	private HttpURLConnection mConnection;

	public ServiceConnectionSE(String url) throws IOException {
		this.mConnection = ((HttpURLConnection) new URL(url).openConnection());
		this.mConnection.setUseCaches(false);
		this.mConnection.setDoOutput(true);
		this.mConnection.setDoInput(true);
	}

	@Override
	public void connect() throws IOException {
		this.mConnection.connect();
	}

	@Override
	public void disconnect() {
		this.mConnection.disconnect();
	}

	@Override
	public void setRequestProperty(String string, String soapAction) {
		this.mConnection.setRequestProperty(string, soapAction);
	}

	@Override
	public void setRequestMethod(String requestMethod) throws IOException {
		this.mConnection.setRequestMethod(requestMethod);
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		return this.mConnection.getOutputStream();
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return this.mConnection.getInputStream();
	}

	@Override
	public InputStream getErrorStream() {
		return this.mConnection.getErrorStream();
	}

	// 设置连接服务器的超时时间,毫秒�?,此为自己添加的方�?
	public void setConnectionTimeOut(int timeout) {
		this.mConnection.setConnectTimeout(timeout);
	}

	@Override
	public String getHost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getResponseCode() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List getResponseProperties() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFixedLengthStreamingMode(int arg0) {
		// TODO Auto-generated method stub

	}

}