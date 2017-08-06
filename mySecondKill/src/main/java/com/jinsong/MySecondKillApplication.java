package com.jinsong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * 部署到服务器上时，入口要继承SpringBootServletInitializer
 * 并且整个项目只能有这里一个main函数
 * 
 * pom.xml中改为<packaging>war</packaging>
 * 
 * cmd进入项目根目录运行以下命令打War包
 * mvn package -Dmaven.test.skip=true
 * @author 188949420@qq.com
 *
 */
@SpringBootApplication
public class MySecondKillApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(MySecondKillApplication.class, args);
	}
}
