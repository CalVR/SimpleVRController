package com.example.simplevrcontroller;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import android.os.AsyncTask;
import android.util.Log;

public class Connection {
	
	private Socket sock;
	private String server;
	private int port;

	public Connection(String server, int port){
		
		this.server = server;
		this.port = port;
		
	}
	
	public void init() throws UnknownHostException, IOException{
		
		if(sock != null)
			sock.close();
		
		sock = new Socket(server, port);
	}
	
	public int send(int num){
		
        try {
			Exception e = new NumSender().execute(num).get();
			if(e != null)
				throw e;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		return -1;
	}
	
    private class NumSender extends AsyncTask<Integer,Void,Exception>{
    	
		private static final int BYTES_TO_SEND = 4;

		@Override
		protected Exception doInBackground(Integer... arg0) {
			for(Integer num : arg0)
				try {
					
					
					byte[] bytes = ByteBuffer.allocate(BYTES_TO_SEND).putInt(num).array();
					
					byte[] reverse = new byte[bytes.length];
					
					for(int y = 0; y < bytes.length; y++)
						reverse[bytes.length - y - 1] = bytes[y];
					
					String tmp = "";
					for(byte b : reverse)
						tmp = tmp + b;
					
					Log.d("Connection",	"Sending " + num + " as " + tmp);
					
					sock.getOutputStream().write(reverse);
					
					//System.out.println(new Scanner(new BufferedInputStream(sock.getInputStream())).nextByte());
				} catch (IOException e) {
					return e;
				}
			
			return null;
		}
    	
    }
    
    public void close(){
    	try {
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
