package com.example.ota.FFmpegCommandManager.test;

import com.example.ota.controller.FFmpegManager;
import com.example.ota.controller.FFmpegManagerImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试
 * @since jdk1.7
 */
public class Test {
	/**
	 * 命令组装器测试
	 * 
	 * @throws InterruptedException
	 */

	// ffmpeg -re -i /home/lee/video.mp4 -vcodec copy -acodec copy -b:v 800k
	// -b:a 32k -f flv rtmp://localhost/videotest

	// -re :
	// 表示使用文件的原始帧率进行读取，因为ffmpeg读取视频帧的速度很快，如果不使用这个参数，ffmpeg可以在很短时间就把video.mp4中的视频帧全部读取完并进行推流，这样就无法体现出视频播放的效果了。官方文档中对这个参数的解释是：
	// -i :这个参数表示输入 ，后面/home/lee/video.mp4 就是输入文件。
	// -vcodec copy : -vcodec表示使用的视频编解码器 ，前缀v表示video。后面紧跟的copy
	// 表示复制使用源文件的视频编解码器，比如原文件的编解码器(codec)是h264，则这里就使用h264。
	// -acodec copy : -acodec表示使用的音频编解码器，前缀a表示audio。后面的copy 表示使用源文件的音频编解码器。
	// -b:v 800k : -b:v表示视频的比特率(bitrate) ，为800k。
	// -b:a 32k : 表示音频的比特率为32k。
	// -f flv : -f表示format
	// ，就是强制输出格式为flv，这一步其实也叫封装(mux)，封装要做的事就是把视频和音频混合在一起，进行同步。紧跟在后面的rtmp://localhost/videotest
	// 表示输出的"文件名"，这个文件名可以是一个本地的文件，也可以指定为rtmp流媒体地址。指定为rtmp流媒体地址后，则ffmpeg就可以进行推流。

	public static void test1() throws InterruptedException {
		FFmpegManager manager = new FFmpegManagerImpl();
		Map<String, String> map = new HashMap<String, String>();
		map.put("appName", "test123");
		// 【rtsp://账号:密码@IP地址:544/cam/realmonitor?channel=1&subtype=0\】【端口号默认544，通道号channel默认1，主码流为
		// 0（即subtype=0），辅码流为1（即subtype=1）】
		// map.put("input",
		// "\"rtsp://admin:dsgbridge@192.168.1.102:554/cam/realmonitor?channel=1&subtype=0\"");
		// ffmpeg -re -i C:\Users\Tang\Desktop\maven\a.mp4 -vcodec h264 -acodec
		// copy -b:v 2048k -b:a 32k -f flv rtmp://localhost/live

		map.put("input", "C:\\Users\\Tang\\Desktop\\maven\\a.mp4");
		map.put("output", "\"rtmp://localhost/live/\"");
		map.put("codec", "h264");
		map.put("fmt", "flv");
		map.put("fps", "25");
		map.put("rs", "640x360");
		map.put("twoPart", "0");
		// 执行任务，id就是appName，如果执行失败返回为null
		String id = manager.start(map);
		System.out.println(id);
		// 通过id查询
		/*
		 * TaskEntity info = manager.query(id); System.out.println(info); //
		 * 查询全部 Collection<TaskEntity> infoList = manager.queryAll();
		 * System.out.println(infoList); Thread.sleep(30000); // 停止id对应的任务
		 * manager.stop(id);
		 */
	}

	/**
	 * 默认方式，rtsp->rtmp转流单个命令测试
	 * 
	 * @throws InterruptedException
	 */

	public static void test2() throws InterruptedException {
		FFmpegManager manager = new FFmpegManagerImpl();
		// -rtsp_transport tcp
		// 测试多个任何同时执行和停止情况
		// 默认方式发布任务
		manager.start("tomcat",
				"ffmpeg -re  -rtsp_transport tcp -i \"rtsp://admin:1234@192.168.0.1/Streaming/Channels/301?transportmode=unicast\" -f flv -vcodec libx264 -vprofile baseline -acodec aac -ar 44100 -strict -2 -ac 1 -f flv -s 1280x720 -q 10 \"rtmp://127.0.0.1:1935/live/test\"");

		//Thread.sleep(30000);
		// 停止全部任务
		//manager.stopAll();
	}

	/**
	 * 完整ffmpeg路径测试
	 * 
	 * @throws InterruptedException
	 */
	public static void test4() throws InterruptedException {
		FFmpegManager manager = new FFmpegManagerImpl();
		// -rtsp_transport tcp
		// 测试多个任何同时执行和停止情况
		// 默认方式发布任务
		manager.start("test", " ffmpeg -i  -vcodec  h264   -f flv -an ", true);

		Thread.sleep(30000);
		// 停止全部任务
		manager.stopAll();
	}

	/**
	 * rtsp-rtmp转流多任务测试
	 * 
	 * @throws InterruptedException
	 */
	public static void test3() throws InterruptedException {
		FFmpegManager manager = new FFmpegManagerImpl();
		// -rtsp_transport tcp
		// 测试多个任何同时执行和停止情况
		// false表示使用配置文件中的ffmpeg路径，true表示本条命令已经包含ffmpeg所在的完整路径
		manager.start("tomcat",
				"ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat",
				false);
		manager.start("tomcat1",
				"ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat1",
				false);
		manager.start("tomcat2",
				"ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat2",
				false);
		manager.start("tomcat3",
				"ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat3",
				false);
		manager.start("tomcat4",
				"ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat4",
				false);
		manager.start("tomcat5",
				"ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat5",
				false);
		manager.start("tomcat6",
				"ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat6",
				false);
		manager.start("tomcat7",
				"ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat7",
				false);
		manager.start("tomcat8",
				"ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat8",
				false);
		manager.start("tomcat9",
				"ffmpeg -i rtsp://184.72.239.149/vod/mp4://BigBuckBunny_175k.mov -vcodec copy -acodec copy -f flv -y rtmp://106.14.182.20:1935/rtmp/tomcat9",
				false);

		Thread.sleep(30000);
		// 停止全部任务
		manager.stopAll();
	}

	public static void main(String[] args) throws InterruptedException {
//		test1();
		// test2();
		// test3();
//		 test4();
		test2();
	}
}
