package com.utils;

import com.jcraft.jsch.SftpProgressMonitor;

/**
 * 文件上传检测
 * @author zhangsl
 * @date 2018-07-24
 */
public class SftpProgressImpl implements SftpProgressMonitor {
	private long size;
	private long currentSize = 0;
	private boolean endFlag = false;
	@Override
	public void init(int op, String srcFile, String dstDir, long size) {
		System.out.println("文件开始上传:" + srcFile + "-->" + dstDir + " ,文件大小:"
				+ size);
		this.size = size;
	}
	@Override
	public void end() {
		System.out.println("文件上传结束");
		endFlag = true;
	}
	@Override
	public boolean count(long count) {
		currentSize += count;
		// System.out.println("上传size:" + currentSize);
		return true;
	}
	public boolean isSuccess() {
		return endFlag && currentSize == size;
	}
}
