package net.roseboy.classfinal;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


/**
 * 加密普通jar，springboot jar，spring web war
 * 启动 java -jar this.jar
 * 启动2 java -jar this.jar -file springboot.jar -libjars a.jar,b.jar -packages net.roseboy,yiyon.com -exclude org.spring -pwd 995800 -Y
 *
 * @author roseboy
 * @date 2019-08-05
 */
public class MainJar {
    public static void main(String[] args) {
        Constants.pringInfo();

        try {
            //先接受参数
            CommandLine cmd = getCmdOptions(args);
            if (cmd == null) {
                return;
            }

            String path = null;//需要加密的class路径
            String packages = null; //包名
            String libjars = null;
            String excludeClass = null;//排除的class
            String password = null;//密码

            if (cmd.hasOption("file")) {
                path = cmd.getOptionValue("file");
            }
            if (cmd.hasOption("libjars")) {
                libjars = cmd.getOptionValue("libjars");
            }
            if (cmd.hasOption("packages")) {
                packages = cmd.getOptionValue("packages");
            }
            if (cmd.hasOption("pwd")) {
                password = cmd.getOptionValue("pwd");
            }
            if (cmd.hasOption("exclude")) {
                excludeClass = cmd.getOptionValue("exclude");
            }

            //没有参数手动输入
            Scanner scanner = new Scanner(System.in);
            if (args == null || args.length == 0) {
                while (path == null || path.length() == 0) {
                    System.out.print("请输入需要加密的jar/war路径:");
                    path = scanner.nextLine();
                }

                System.out.print("请输入jar/war包lib下要加密jar文件名(多个用\",\"分割):");
                libjars = scanner.nextLine();

                System.out.print("请输入需要加密的包名(可为空,多个用\",\"分割):");
                packages = scanner.nextLine();

                System.out.print("请输入需要排除的类名(可为空,多个用\",\"分割):");
                excludeClass = scanner.nextLine();

                while (password == null || password.length() == 0) {
                    System.out.print("请输入加密密码:");
                    password = scanner.nextLine();
                }
            }

            //test数据
            if ("123123".equals(path)) {
                //springboot jar
                //path = "/Users/roseboy/work-yiyon/易用框架/yiyon-server-liuyuan/yiyon-package-liuyuan/target/yiyon-package-liuyuan-1.0.0.jar";
                //spring web war
                path = "/Users/roseboy/work-yiyon/北大口腔/erpbeidakouqiang/target/erpbeidakouqiang-1.0.0.war";
                //fat jar
                //path = "/Users/roseboy/code-space/agent/target/agent-1.0.jar";

                libjars = "yiyon-basedata-1.0.0.jar,jeee-admin-1.0.0.jar,aspectjweaver-1.8.13.jar";
                packages = "com.yiyon,net.roseboy,yiyon";//包名过滤
                excludeClass = "org.spring";//排除的类
                password = "000000";
            }


            System.out.println();
            System.out.println("加密信息如下:");
            System.out.println("-------------------------");
            System.out.println("jar/war路径:    " + path);
            System.out.println("lib下的jar:      " + libjars);
            System.out.println("包名:           " + packages);
            System.out.println("排除的类名:      " + excludeClass);
            System.out.println("密码:           " + password);
            System.out.println("-------------------------");
            System.out.println();

            String yes;
            if (cmd.hasOption("Y")) {
                yes = "Y";
            } else {
                System.out.println("请牢记密码，密码忘记将无法启动项目。确定执行吗？(Y/n)");
                yes = scanner.nextLine();
                while (!"n".equals(yes) && !"Y".equals(yes)) {
                    System.out.println("Yes or No ？(Y/n)");
                    yes = scanner.nextLine();
                }
            }

            if ("Y".equals(yes)) {
                List<String> includeJars = new ArrayList<>();
                includeJars.add("-");
                if (libjars != null && libjars.length() > 0) {
                    includeJars.addAll(Arrays.asList(libjars.split(",")));
                }
                //加密过程
                System.out.println("处理中...");
                JarEncryptor decryptor = new JarEncryptor();
                String result = decryptor.doEncryptJar(path, packages, includeJars, excludeClass, password);
                System.out.println("加密完成，请牢记密码！");
                System.out.println(result);
            } else {
                System.out.println("已取消！");
            }
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * cmd 参数
     *
     * @return
     */
    public static CommandLine getCmdOptions(String[] args) {
        CommandLine cmd = null;
        Options options = new Options();
        options.addOption("classes", true, "加密的classes路径");
        options.addOption("libs", true, "项目依赖的jar包目录(多个用\",\"分割)");
        options.addOption("packages", true, "加密的包名(可为空,多个用\",\"分割)");
        options.addOption("pwd", true, "加密密码");
        options.addOption("exclude", true, "排除的类名(可为空,多个用\",\"分割)");
        options.addOption("file", true, "加密的jar/war路径");
        options.addOption("libjars", true, "jar/war lib下的jar(多个用\",\"分割)");
        options.addOption("Y", false, "无需确认");
        options.addOption("C", false, "加密class目录");

        try {
            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse(options, args);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return cmd;
    }

}