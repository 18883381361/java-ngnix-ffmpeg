package com.example.ota.FFmpegCommandManager.service;

import java.util.Map;
/**
 * 命令组装器接口
 */
public interface CommandAssembly {
	/**
	 * 将参数转为ffmpeg命令
	 * @param paramMap
	 * @return
	 */
	public String assembly(Map<String, String> paramMap);
}
