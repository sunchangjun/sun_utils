package com.utils;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;

public class SshFileUtils {

    private Session session;
    private ChannelSftp sftp;
    private ChannelShell shell;
    public SshFileUtils(String  host,String username,String password,int port) throws JSchException {
        openSession(host, username, password,port);
    }
    /**
     * 开启session
     * @param host
     * @param username
     * @param password
     * @return
     * @throws JSchException
     */
    private Session openSession(String host, String username, String password,Integer port)
            throws JSchException {
        JSch jsch = new JSch();
        if(port == null){
            session = jsch.getSession(username, host);
        }else{
            session = jsch.getSession(username, host, port);
        }
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        session.setConfig(sshConfig);
        session.setPassword(password);
        session.connect(10000);
        return session;
    }
    /**
     * 上传本地文件到远程linux上 使用sftp上传,完成后关闭channel
     * @param localFile  本地war文件全路径
     * @param remoteFile  远端目录路径
     * @return 上传成功返回true,否则false
     * @throws Exception
     */
    public boolean transfer(String localFile, String remoteFile) throws Exception {
        sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();
        String parent = getParentPath(remoteFile);
        try{
            sftp.stat(parent);//检测是否文件夹存在,如果不存在则抛出异常
        }catch(Exception e){
            executeCommand("mkdir -p " + parent);
        }
        SftpProgressImpl sftpProgressMonitorImpl = new SftpProgressImpl();
        sftp.put(localFile, remoteFile, sftpProgressMonitorImpl);
        sftp.disconnect();
        return sftpProgressMonitorImpl.isSuccess();
    }

    public boolean transfer(InputStream is, String remoteFile) throws Exception {
        sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();
        String parent = getParentPath(remoteFile);
        try{
            sftp.stat(parent);//检测是否文件夹存在,如果不存在则抛出异常
        }catch(Exception e){
            executeCommand("mkdir -p " + parent);
        }
        SftpProgressImpl sftpProgressMonitorImpl = new SftpProgressImpl();
        sftp.put(is, remoteFile, sftpProgressMonitorImpl);
        sftp.disconnect();
        return sftpProgressMonitorImpl.isSuccess();
    }


    public void downFile(String ftpFilePath, String optPutFile) throws Exception {
        InputStream inputStream= null;
        OutputStream output = null;
        try {
            sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect();
            inputStream= sftp.get(ftpFilePath);
            output = new FileOutputStream(optPutFile);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        }catch(Exception e){
            e.printStackTrace();
        }  finally{
            inputStream.close();
            output.close();
            sftp.disconnect();
        }


    }

    /**
     * 执行命令,完成后立刻关闭pipe
     * @param command
     * @throws Exception
     */
    public void executeCommand(String command) throws Exception {
        shell = (ChannelShell) session.openChannel("shell");
        shell.connect();
        System.out.println("Sending commands...");
        PrintStream out = new PrintStream(shell.getOutputStream());
        out.println(command);
        out.println("exit");
        out.flush();
        readChannelOutput(shell);
        System.out.println("Finished sending commands!");
        shell.disconnect();
    }

    /**
     * 读取shell的实时输出
     * @param channel
     * @throws Exception
     */
    private void readChannelOutput(Channel channel) throws Exception {
        byte[] buffer = new byte[1024];
        InputStream in = channel.getInputStream();
        while (true) {
            while (in.available() > 0) {
                int i = in.read(buffer, 0, 1024);
                if (i < 0)break;
                System.out.print(new String(buffer, 0, i));
            }
            if (channel.isClosed())break;
            Thread.sleep(1000);
        }
    }

    private static String getParentPath(String path){
        if(path.endsWith("/")){
            throw new IllegalArgumentException("远端路径["+path+"]有误,这似乎不是一个合法的文件路径!");
        }
        int pos = path.lastIndexOf("/");
        return path.substring(0, pos);
    }

    public void close() {
        if(this.sftp!=null && !sftp.isClosed()){
            sftp.disconnect();
        }
        if(this.shell!=null && !shell.isClosed()){
            shell.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }


}
