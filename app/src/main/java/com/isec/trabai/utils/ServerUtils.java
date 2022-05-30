package com.isec.trabai.utils;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class ServerUtils {

    private static final String TAG = "ServerUtils";

    public static void sendFile(String pathname, String filename) {
        new LongOperation().execute(pathname, filename);
    }

    private static class LongOperation extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                JSch ssh = new JSch();
                Session session = ssh.getSession("amistudent", "193.137.203.71", 22);

                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.setPassword("ami#_2022");

                session.connect();
                Channel channel = session.openChannel("sftp");
                channel.connect();

                ChannelSftp sftp = (ChannelSftp) channel;

                sftp.cd("data/profile2/P2_LuisSilva_VitorFernandes");

                sftp.put(params[0] + "/" + params[1], params[1]);
                channel.disconnect();
                session.disconnect();
            } catch (JSchException e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
                return false;
            } catch (SftpException e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d(TAG, "File sent: " + result.toString());
        }

    }
}
