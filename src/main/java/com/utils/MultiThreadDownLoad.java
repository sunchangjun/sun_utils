package com.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 根据输入的url和设定的线程数，来完成断点续传功能
 * @author zhangsl
 * @date 2018年12月29日
 */
public class MultiThreadDownLoad {
	private String address;
	private String dir;
	private String filename;
	private String tmpfilename;
	private int threadNum = 0;
	private CountDownLatch latch;// 设置一个计数器，代码内主要用来完成对缓存文件的删除
	private long fileLength = 0l;
	private long threadLength = 0l;
	private long[] startPos;// 保留每个线程下载数据的起始位置。
	private long[] endPos;// 保留每个线程下载数据的截止位置。
	private boolean bool = false;
	private URL url;

	public MultiThreadDownLoad(String address, int threadNum, String dir) {
		this.address = address;
		this.threadNum = threadNum;
		this.dir = dir;
		this.startPos = new long[threadNum];
		this.endPos = new long[threadNum];
		this.latch = new CountDownLatch(threadNum);
	}

	/**
	 * 断点续传下载文件
	 * @return
	 */
	public String downloadPart() {
		// 从文件链接中获取文件名，此处没考虑文件名为空的情况，此种情况可能需使用UUID来生成一个唯一数来代表文件名。
		filename = this.dir + address.substring(address.lastIndexOf('/') + 1, address.contains("?") ? address.lastIndexOf('?')
				: address.length());
		tmpfilename = filename + "_tmp";
		File file = new File(filename);//相对目录
		File tmpfile = new File(tmpfilename);
		try {
			url = new URL(address);
			HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
			httpcon.setRequestMethod("GET");
			fileLength = httpcon.getContentLengthLong();

			// 每个线程需下载的资源大小；由于文件大小不确定，为避免数据丢失
			threadLength = fileLength % threadNum == 0 ? fileLength / threadNum : fileLength / threadNum + 1;
			// 打印下载信息
			//System.out.println("fileName: " + filename + " ," + "fileLength= " + fileLength + " the threadLength= " + threadLength);
			// 各个线程在exec线程池中进行，起始位置--结束位置
			if (file.exists() && file.length() == fileLength) {
				System.out.println("文件已存在!!");
				return this.filename;
			} else {
				setBreakPoint(startPos, endPos, tmpfile);
				ExecutorService exec = Executors.newCachedThreadPool();
				for (int i = 0; i < threadNum; i++) {
					exec.execute(new DownLoadThread(startPos[i], endPos[i], this, i, tmpfile, latch));
				}
				latch.await();// 当你的计数器减为0之前，会在此处一直阻塞。
				exec.shutdown();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// 下载完成后，判断文件是否完整，并删除临时文件
		if (file.length() == fileLength) {
			if (tmpfile.exists()) {
				System.out.println("删除临时文件!!");
				boolean result = tmpfile.delete();
				if(!result){//无法删除时再休息一下
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					result = tmpfile.delete();
					System.out.println("删除文件:"+tmpfile.getAbsolutePath()+" 结果:"+result);
				}
			}
		}
		return this.filename;
	}

	/**
	 * 断点设置方法，当有临时文件时，直接在临时文件中读取上次下载中断时的断点位置。没有临时文件，即第一次下载时，重新设置断点
	 * @param spos 线程下载开始位置
	 * @param epos 结程下载结束位置
	 * @param tmpfile
	 */
	private void setBreakPoint(long[] spos, long[] epos, File tmpfile) {
		RandomAccessFile rantmpfile = null;
		try {
			if (tmpfile.exists()) {//文件以前下载过,由于某种原因中断了
				System.out.println("继续下载!!");
				rantmpfile = new RandomAccessFile(tmpfile, "rw");
				for (int i = 0; i < threadNum; i++) {
					spos[i] = readLong(rantmpfile, 8*i+8);
					epos[i] = readLong(rantmpfile, 8*(i + 1000) + 16);
					System.out.println("the Array content in the exit file: ");
					System.out.println("thre thread" + (i + 1) + " startPos:" + spos[i] + ", endPos: " + epos[i]);
				}
			} else {//首次下载这个文件
				System.out.println("the tmpfile is not available!!");
				rantmpfile = new RandomAccessFile(tmpfile, "rw");
				// 最后一个线程的截止位置大小为请求资源的大小
				for (int i = 0; i < threadNum; i++) {
					spos[i] = threadLength * i;
					if (i == threadNum - 1) {
						epos[i] = fileLength;
					} else {
						epos[i] = threadLength * (i + 1) - 1;
					}
					writeLong(rantmpfile, 8 * i + 8, spos[i]);
					writeLong(rantmpfile, 8 * (i + 1000) + 16, epos[i]);
					System.out.println("the Array content: ");
					System.out.println("thre thread" + (i + 1) + " startPos:" + spos[i] + ", endPos: " + epos[i]);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rantmpfile != null) {
					rantmpfile.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static long readLong(RandomAccessFile accessFile, long seekpos) throws IOException{
		accessFile.seek(seekpos);
		return accessFile.readLong();
	}
	
	private static void writeLong(RandomAccessFile accessFile, long seekpos, long value) throws IOException{
		accessFile.seek(seekpos);
		accessFile.writeLong(value);
	}

	/**
	 * 实现下载功能的内部类，通过读取断点来设置向服务器请求的数据区间
	 * @author zhangsl
	 * @date 2018年12月29日
	 */
	class DownLoadThread implements Runnable {

		private long spos;
		private long epos;
		private MultiThreadDownLoad task;
		private RandomAccessFile downloadfile;
		private int id;
		private File tmpfile;
		private RandomAccessFile rantmpfile;
		private CountDownLatch latch;

		public DownLoadThread(long spos, long epos, MultiThreadDownLoad task, int id, File tmpfile,
				CountDownLatch latch) {
			this.spos = spos;
			this.epos = epos;
			this.task = task;
			this.tmpfile = tmpfile;
			try {
				this.downloadfile = new RandomAccessFile(this.task.filename, "rw");
				this.rantmpfile = new RandomAccessFile(this.tmpfile, "rw");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			this.id = id;
			this.latch = latch;
		}

		@Override
		public void run() {
			HttpURLConnection httpcon = null;
			InputStream is = null;
			System.out.println("线程" + id + " 开始下载!!");
			while (true) {
				try {
					httpcon = (HttpURLConnection) task.url.openConnection();
					httpcon.setRequestMethod("GET");
					// 防止网络阻塞，设置指定的超时时间；单位都是ms。超过指定时间，就会抛出异常
					httpcon.setReadTimeout(20000);// 读取数据的超时设置
					httpcon.setConnectTimeout(20000);// 连接的超时设置
					if (spos < epos) {
						// 向服务器请求指定区间段的数据，这是实现断点续传的根本。
						httpcon.setRequestProperty("Range", "bytes=" + spos + "-" + epos);
						System.out.println("线程 " + id + " 长度:---- " + (epos - spos));
						downloadfile.seek(spos);
						if (httpcon.getResponseCode() != HttpURLConnection.HTTP_OK
								&& httpcon.getResponseCode() != HttpURLConnection.HTTP_PARTIAL) {
							this.task.bool = true;//是否完成的标记?
							httpcon.disconnect();
							downloadfile.close();
							System.out.println("线程 ---" + id + " 下载完成!!");
							latch.countDown();// 计数器自减
							break;
						}
						is = httpcon.getInputStream();// 获取服务器返回的资源流
						long count = 0l;
						byte[] buf = new byte[1024*150];//150k
						int len = 0;
						while (!this.task.bool && (len = is.read(buf)) != -1) {
							count += len;
							downloadfile.write(buf, 0, len);
							// 不断更新每个线程下载资源的起始位置，并写入临时文件；为断点续传做准备
							spos += len;
							writeLong(rantmpfile, 8*id+8, spos);
						}
						System.out.println("线程 " + id + " 总下载大小: " + count);
						// 关闭流
						is.close();
						httpcon.disconnect();
						downloadfile.close();
						rantmpfile.close();
					}
					latch.countDown();// 计数器自减
					System.out.println("线程 " + id + " 下载完成!!");
					break;
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("线程 " + id + " 发生异常,等待10秒");
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				} finally {
					try {
						if (is != null) {
							is.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
