package generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;

import java.util.Collections;
import java.util.function.Consumer;

public class MPGenerator {

    public static String url = "jdbc:mysql://127.0.0.1:3306/im?characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false";

    public static String username = "root";

    public static String password = "root";

    public static void main(String[] args) {

        // 全局配置
        Consumer<GlobalConfig.Builder> globalConfig = builder -> {
            builder.author("bx")
                    // 覆盖
                    .fileOverride()
                    // 生成的文件放到哪个路径下
                    .outputDir("E:\\AAAFrequently-used\\Project\\OkPrj\\im\\mp-generator\\src\\main\\java");
        };

        // 包路径配置
        Consumer<PackageConfig.Builder> packageConfig = builder -> {
            // 指定生成文件的包名
            builder.parent("com.bx.im")
                    // 在父包名下再指定一级路径
                    // .moduleName("mp")
                    // mapper.xml文件路径
                    .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "E:\\AAAFrequently-used\\Project\\OkPrj\\im\\mp-generator\\src\\main\\resources\\mapper"));
        };

        // 策略配置
        Consumer<StrategyConfig.Builder> strategyConfig = builder -> {
            // 指定要生成文件的表名
            // builder.addInclude("user_friend", "friend_msg", "friend_ask");
            builder.addInclude("group_users");
                // 过滤表前缀
                // .addTablePrefix("t_", "c_");
        };

        FastAutoGenerator.create(url, username, password)
                .globalConfig(globalConfig)
                .packageConfig(packageConfig)
                .strategyConfig(strategyConfig)
                .execute();
    }
}
