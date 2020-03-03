package com.example.ota.controller;

import com.example.ota.FFmpegCommandManager.config.FFmpegConfig;
import com.example.ota.FFmpegCommandManager.dao.TaskDao;
import com.example.ota.FFmpegCommandManager.entity.TaskEntity;
import com.example.ota.FFmpegCommandManager.service.CommandAssembly;
import com.example.ota.FFmpegCommandManager.service.TaskHandler;

import java.util.Collection;
import java.util.Map;

import static com.example.ota.FFmpegCommandManager.util.PropertiesUtil.load;

/**
 * FFmpeg命令操作管理器，可执行FFmpeg命令/停止/查询任务信息
 * 
 * @since jdk1.7  loadFFmpeg.properties
 */
public interface FFmpegManager {
	
	public static FFmpegConfig config=(FFmpegConfig) load("defaultFFmpegConfig.properties",  FFmpegConfig.class);
	/**
	 * 注入自己实现的持久层
	 * 
	 * @param taskDao
	 */
	public void setTaskDao(TaskDao taskDao);

	/**
	 * 注入ffmpeg命令处理器
	 * 
	 * @param taskHandler
	 */
	public void setTaskHandler(TaskHandler taskHandler);

	/**
	 * 注入ffmpeg命令组装器
	 * 
	 * @param commandAssembly
	 */
	public void setCommandAssembly(CommandAssembly commandAssembly);

	/**
	 * 通过命令发布任务（默认命令前不加FFmpeg路径）
	 * 
	 * @param id - 任务标识
	 * @param command - FFmpeg命令
	 * @return
	 */
	public String start(String id, String command);
	/**
	 * 通过命令发布任务
	 * @param id - 任务标识
	 * @param commond - FFmpeg命令
	 * @param hasPath - 命令中是否包含FFmpeg执行文件的绝对路径
	 * @return
	 */
	public String start(String id, String commond, boolean hasPath);

	/**
	 * 通过组装命令发布任务
	 *
	 * @param assembly
	 *            -组装命令（详细请参照readme文档说明）
	 * @return
	 */
	public String start(Map<String, String> assembly);
	
	/**
	 * 停止任务
	 * 
	 * @param id
	 * @return
	 */
	public boolean stop(String id);

	/**
	 * 停止全部任务
	 * 
	 * @return
	 */
	public int stopAll();

	/**
	 * 通过id查询任务信息
	 * 
	 * @param id
	 */
	public TaskEntity query(String id);

	/**
	 * 查询全部任务信息
	 * 
	 */
	public Collection<TaskEntity> queryAll();
}
