package com.example.ota.controller;

import com.example.ota.common.utils.R;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
@CrossOrigin
@RestController
public class CameraController {
	public static Map<String, String> maps = new HashMap<>();
	public static FFmpegManager manager = new FFmpegManagerImpl();

	// 启动视频转码
	@RequestMapping(value = "/zhuanma")
	public R zhuanma(@RequestParam( defaultValue = "") String ip,
					 @RequestParam(defaultValue = "") String chanel,
					 @RequestParam(defaultValue = "") String username,
					 @RequestParam(defaultValue = "") String password,
					 @RequestParam(defaultValue = "") String starttime,
					 @RequestParam(defaultValue = "") String endtime,
					 @RequestParam(defaultValue = "") String type) {
		manager.stopAll();
		try {
			//FFmpegManager manager = new FFmpegManagerImpl();
			// -rtsp_transport tcp
			// 测试多个任何同时执行和停止情况
			// 默认方式发布任务
			String id =null;
			if(type.equals("1")){//实时预览
				id =manager.start("tomcat",
						"ffmpeg -re  -rtsp_transport tcp -i \"rtsp://"+username+":"+password+"@"+ip+"/Streaming/Channels/"+chanel+"01?transportmode=unicast\" -f flv -vcodec libx264 -vprofile baseline -acodec aac -ar 44100 -strict -2 -ac 1 -f flv -s 1280x720 -q 10 \"rtmp://127.0.0.1:1935/live/test\"");
			}else if(type.equals("2")){//历史回放
				id =manager.start("tomcat",
						"ffmpeg -re  -rtsp_transport tcp -i \"rtsp://"+username+":"+password+"@"+ip+"/Streaming/tracks/"+chanel+"01?starttime="+starttime+"&endtime="+endtime+"\" -f flv -r 25 -s 1280x720 -an \"rtmp://127.0.0.1:1935/live/test\"");
			}

			// 执行任务，id就是appName，如果执行失败返回为null
			// 将转流放入静态map中用于关转流
			maps.put(chanel + ip, id);

			// start(String id,String commond,boolean hasPath);
			// false表示使用配置文件中的ffmpeg路径，true表示本条命令已经包含ffmpeg所在的完整路径
			//  ffmpeg -i
			// "rtsp://admin:dsgbridge@192.168.1.102:554//cam/realmonitor?channel=1&subtype=0"
			// -vcodec  h264(copy)   -f flv -an "rtmp://localhost/live"

			// manager.start("camera2",
			// "ffmpeg -i C:\\Users\\Tang\\Desktop\\test\\1.mp4 -vcodec h264
			// -acodec copy -f flv -an
			// \"rtmp://192.168.1.109:1935/live/\"camera2",
			// false);
			// manager.stop(id);


			// 将转流放入静态map中用于关转流


			System.out.println(chanel + ip + "流已开启");

		} catch (Exception e) {
			e.getStackTrace();
		}
		return R.ok();
	}

	// 关闭转流
	@RequestMapping(value = "/guanbi")
	public void guanbi(@RequestParam( defaultValue = "") String ip,
					   @RequestParam(defaultValue = "") String chanel) {

		try {
			manager.stop(maps.get(chanel+ip));
			System.out.println(chanel+ip + "流已关闭");
		} catch (Exception e) {
			e.getStackTrace();
		}

	}

	// 开转流
	@RequestMapping(value = "/kai")
	public void kai(HttpServletResponse response, HttpServletRequest request) {
		// ffmpeg -re -i C:\Users\Tang\Desktop\test\2.mp4 -vcodec copy -acodec
		// copy -b:v 800k -b:a 32k -f flv rtmp://localhost/live
		manager.start("camera1",
				"ffmpeg -re -i C:\\Users\\Tang\\Desktop\\test\\1.mp4 -vcodec h264 -acodec copy  -b:v 800k -b:a 32k -f flv -an \"rtmp://192.168.1.109:1935/live/\"camera1",
				false);
		maps.put("camera1", "camera1");
		manager.start("camera2",
				"ffmpeg -re -i C:\\Users\\Tang\\Desktop\\test\\2.mp4 -vcodec h264 -acodec copy  -b:v 800k -b:a 32k -f flv -an \"rtmp://192.168.1.109:1935/live/\"camera2",
				false);
		maps.put("camera2", "camera2");
		manager.start("camera3",
				"ffmpeg -re -i C:\\Users\\Tang\\Desktop\\test\\3.mp4 -vcodec h264 -acodec copy  -b:v 800k -b:a 32k -f flv -an \"rtmp://192.168.1.109:1935/live/\"camera3",
				false);
		maps.put("camera3", "camera3");
	}

	// 开转流
	@RequestMapping(value = "/ffmpeg")
	public void ffmpeg(HttpServletResponse response, HttpServletRequest request) {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("ffmpeg.properties");
		Properties p = new Properties();
		try {
			p.load(inputStream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// System.out.println("ip:" + p.getProperty("ip") + ",port:" +
		// p.getProperty("port"));
		Map<String, String> map = new HashMap<>();
		map.put("appName", p.getProperty("code"));// code为客户端编码
		// map.put("input", "\"rtsp://admin:dsgbridge@" + ip +
		// "/cam/realmonitor?channel=1&subtype=0\"");
		map.put("output", p.getProperty("output"));
		map.put("codec", p.getProperty("codec"));
		map.put("fmt", p.getProperty("fmt"));
		map.put("fps", p.getProperty("fps"));
		map.put("rs", p.getProperty("rs"));
		map.put("twoPart", p.getProperty("twoPart"));// twoPart：0-推一个元码流；1-推一个自定义推流；2-推两个流（一个是自定义，一个是元码）

		map.put("input", p.getProperty("input"));
		// 执行任务，id就是appName，如果执行失败返回为null
		String id = manager.start(map);
		// 将转流放入静态map中用于关转流
		maps.put(p.getProperty("code"), id);
		System.out.println("appName: " + p.getProperty("code"));
		System.out.println("input： " + p.getProperty("input"));
		System.out.println("output: " + p.getProperty("output"));
		System.out.println("codec: " + p.getProperty("codec"));
		System.out.println("fmt: " + p.getProperty("fmt"));
		System.out.println("fps: " + p.getProperty("fps"));
		System.out.println("rs： " + p.getProperty("rs"));
		System.out.println("twoPart：" + p.getProperty("twoPart"));
		System.out.println();
		System.out.println(p.getProperty("code") + ":  流已开启");
	}

	// 开转流
	@RequestMapping(value = "/ffmpegs")
	public void ffmpegs(HttpServletResponse response, HttpServletRequest request) {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("ffmpeg.properties");
		Properties p = new Properties();
		try {
			p.load(inputStream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String number = p.getProperty("number");
		int num = Integer.valueOf(number);
		// System.out.println("ip:" + p.getProperty("ip") + ",port:" +
		// p.getProperty("port"));
		for (int i = 1; i <= num; i++) {
			// maps.remove("a");

			Map<String, String> map = new HashMap<>();
			map.put("appName", p.getProperty("code" + i));// code为客户端编码
			// map.put("input", "\"rtsp://admin:dsgbridge@" + ip +
			// "/cam/realmonitor?channel=1&subtype=0\"");
			map.put("output", p.getProperty("output" + i));
			map.put("codec", p.getProperty("codec" + i));
			map.put("fmt", p.getProperty("fmt" + i));
			map.put("fps", p.getProperty("fps" + i));
			map.put("rs", p.getProperty("rs" + i));
			map.put("kb", p.getProperty("kb" + i));
			map.put("twoPart", p.getProperty("twoPart" + i));// twoPart：0-推一个元码流；1-推一个自定义推流；2-推两个流（一个是自定义，一个是元码）

			map.put("input", p.getProperty("input" + i));

			if(maps.containsKey(p.getProperty("code" + i))==false){
				// 执行任务，id就是appName，如果执行失败返回为null
				String id = manager.start(map);
				// 将转流放入静态map中用于关转流
				maps.put(p.getProperty("code" + i), id);
				System.out.println("appName: " + p.getProperty("code" + i));
				System.out.println("input： " + p.getProperty("input" + i));
				System.out.println("output: " + p.getProperty("output" + i));
				System.out.println("codec: " + p.getProperty("codec" + i));
				System.out.println("fmt: " + p.getProperty("fmt" + i));
				System.out.println("fps: " + p.getProperty("fps" + i));
				System.out.println("rs： " + p.getProperty("rs" + i));
				System.out.println("kb" + p.getProperty("kb" + i));
				System.out.println("twoPart：" + p.getProperty("twoPart" + i));
				System.out.println();
				System.out.println(p.getProperty("code" + i) + ":  流已开启");
			}
		}
	}

	// 关闭转流
	@RequestMapping(value = "/offall")
	public void offall(HttpServletResponse response, HttpServletRequest request) {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("ffmpeg.properties");
		Properties p = new Properties();
		try {
			p.load(inputStream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String number = p.getProperty("number");
		int num = Integer.valueOf(number);
		for (int i = 1; i <= num; i++) {
			try {
				manager.stop(maps.get(p.getProperty("code" + i)));
				maps.remove(p.getProperty("code" + i));
				System.out.println(p.getProperty("code" + i) + "流已关闭");
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
	}

	@RequestMapping(value = "/index")
	public String index(HttpServletResponse response, HttpServletRequest request) {
		return "index";
	}
	
	@RequestMapping(value = "/getNginxUrls")
	public List<String> getNginxUrls(HttpServletResponse response, HttpServletRequest request) {
		List<String> list =new ArrayList<String>();
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("ffmpeg.properties");
		Properties p = new Properties();
		try {
			p.load(inputStream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String number = p.getProperty("number");
		int num = Integer.valueOf(number);
		for (int i = 1; i <= num; i++) {
			try {
				String output = p.getProperty("output"+i);
				output=output.replace("\"","");
				String url = output+p.getProperty("code"+i);
				System.out.println(url);
				list.add(url);
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
		for (String string : list) {
			System.out.println(string+"    1");
		}
		return list;
	}
	
	@RequestMapping(value = "/aa")
	public String index1(HttpServletResponse response, HttpServletRequest request) throws IOException, InterruptedException {
				String[] cmds = {"/bin/sh","-c","ps -ef|grep java"};
				Process pro = Runtime.getRuntime().exec(cmds);
				pro.waitFor();
				InputStream in = pro.getInputStream();
				BufferedReader read = new BufferedReader(new InputStreamReader(in));
				String line = null;
				while((line = read.readLine())!=null){
					System.out.println(line);
				}
		return null;
	}
}
