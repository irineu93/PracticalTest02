package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket != null) {
            try {
                BufferedReader bufferedReader = Utilities.getReader(socket);
                PrintWriter printWriter = Utilities.getWriter(socket);
                if (bufferedReader != null && printWriter != null) {
                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type)!");


                    String alarmInfo = bufferedReader.readLine();

                    HashMap<String, AlarmInfo> data = serverThread.getData();

                    AlarmInfo weatherForecastInformation = null;

                    if (alarmInfo != null && !alarmInfo.isEmpty()) {

                        String ipAddress = socket.getInetAddress().toString();

                        String tokens[] = alarmInfo.split(":");
                        String action = tokens[0];

                        if (action.equals(Constants.SET)) {

                            int hour = Integer.parseInt(tokens[1]);
                            int minutes = Integer.parseInt(tokens[2]);

                            serverThread.setData(ipAddress, new AlarmInfo(hour, minutes));

                            printWriter.println("Alarm activated");
                            printWriter.flush();
                        }
                        else if (action.equals(Constants.RESET)) {
                            serverThread.clearData();

                            printWriter.println("Alarm cleared");
                            printWriter.flush();
                        }
                        else if (action.equals(Constants.POLL)) {

                            if (!data.containsKey(ipAddress)) {
                                printWriter.println("No alarm");
                                printWriter.flush();
                            }
                            else {
                                AlarmInfo alarm = data.get(ipAddress);
                                if (!alarm.isAlarmActive()) {
                                    printWriter.println("Alarm inactive");
                                    printWriter.flush();
                                } else {

                                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                                    HttpClient httpClient = new DefaultHttpClient();
                                    HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS);
                                    List<NameValuePair> params = new ArrayList<>();

                                    ResponseHandler<String> responseHandler = new BasicResponseHandler();

                                    String response = httpClient.execute(httpGet, responseHandler);
                                    if (response != null) {
                                        String newTokens[] = response.split("T");
                                        String new2Tokens[] = newTokens[1].split(":");

                                        int hour = Integer.parseInt(new2Tokens[0]);
                                        int minutes = Integer.parseInt(new2Tokens[1]);

                                        if (hour > alarm.getHour()) {
                                            alarm.setInactive();
                                            printWriter.println("Alarm inactive from now");
                                            printWriter.flush();
                                        } else if (hour == alarm.getHour() && minutes > alarm.getMinutes()) {
                                            alarm.setInactive();
                                            printWriter.println("Alarm inactive from now");
                                            printWriter.flush();
                                        } else {
                                            printWriter.println("Alarm still active");
                                            printWriter.flush();
                                        }
                                    }

                                }
                            }
                        }

                    }

                } else {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] BufferedReader / PrintWriter are null!");
                }
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());

            }
        } else {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
        }
    }

}
