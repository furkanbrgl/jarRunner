import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import model.CmdObj;
import model.Credential;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;

public class RunnerHelper {

    private static final Logger logger = Logger.getLogger(RunnerHelper.class);

/*
    public void runner(CmdObj cmdObj) throws Exception {

        StringBuilder cmdLog = new StringBuilder();
        StringBuilder message = new StringBuilder();
        BufferedReader br = null;
        String cmdOutputLine;

        Process subProcess = Runtime.getRuntime().exec(this.generateMavenCommand(cmdObj));
        br = new BufferedReader(new InputStreamReader(subProcess.getInputStream()));

        while ((cmdOutputLine = br.readLine()) != null) {
            logger.info(System.currentTimeMillis() + "  -  "+ cmdOutputLine);
            //TOOD: If any exception happens, need to tweak them here.
        }

    }

 */

    public void runner(Credential credential, CmdObj cmdObj) throws Exception {

        Session session = null;
        ChannelExec channel = null;

        try {
            session = new JSch().getSession(credential.getUserName(), credential.getIp(), Integer.parseInt(credential.getPort()));
            session.setPassword(credential.getPass());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();

            ///Upload the pic to server
            sftpChannel.put("jarRunner/src/main/images/img.png", "/var/www/rug-counter/outputImages/testimage.jpeg");


            //if the uploading is succesfull, run the cmd for the jar
            channel = (ChannelExec) session.openChannel("exec");

            channel.setCommand(this.generateCommand(cmdObj, credential)[2]);
            logger.info("Command To Run : " + this.generateCommand(cmdObj, credential)[2]);
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            channel.setOutputStream(responseStream);
            channel.connect();

            while (channel.isConnected()) {
                Thread.sleep(100);
            }

            String responseString = new String(responseStream.toByteArray());

            //See how many rug we got.
            System.out.println(responseString);


            //pull the processed image from server.
            sftpChannel.get("/var/www/rug-counter/outputImages/testimage.jpeg", "jarRunner/src/main/images/imgProcessed.png");


        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
    }
    private String[] generateCommand(CmdObj cmdObj, Credential credential) throws Exception {

        StringBuilder cmd = new StringBuilder()
                .append("cd").append(" ")
                .append(credential.getProjectPath() + "jars")
                .append("&&")
                .append("java")
                .append(" ")
                .append("-jar")
                .append(" ")
                .append("-Djava.library.path=")
                .append(cmdObj.getOpenCvPath())
                .append(" ")
                .append("-DtrainedModel=")
                .append(cmdObj.getTrainedModel())
                .append(" ")
                .append("-DinputImgPath=")
                .append(cmdObj.getInputImagePath())
                .append(" ")
                .append("-DoutImgPath=")
                .append(cmdObj.getOutputImagePath())
                .append(" ")
                .append(cmdObj.getJarName());

        return new String[]{"cmd.exe", "/c", cmd.toString()};
    }

}


