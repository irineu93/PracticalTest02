package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientThread extends Thread {

    private String address;
    private int port;
    private String alarmInfo;
    private TextView alarmResultTextView;

    private Socket socket;

    public ClientThread(
            String address,
            int port,
            String alarmInfo,
            TextView alarmResultTextView) {
        this.address = address;
        this.port = port;
        this.alarmInfo = alarmInfo;
        this.alarmResultTextView = alarmResultTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
            }

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader != null && printWriter != null) {
                printWriter.println(alarmInfo);
                printWriter.flush();
                String alarmResult;

                alarmResult = bufferedReader.readLine();
                if (alarmResult != null) {
                    final String finalizedAlarmResult = alarmResult;
                    alarmResultTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            alarmResultTextView.setText(finalizedAlarmResult + "\n");
                        }
                    });
                }

            } else {
                Log.e(Constants.TAG, "[CLIENT THREAD] BufferedReader / PrintWriter are null!");
            }
            socket.close();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
        }
    }

}