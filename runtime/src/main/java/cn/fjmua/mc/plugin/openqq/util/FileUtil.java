package cn.fjmua.mc.plugin.openqq.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Slf4j
public class FileUtil {

    public static String readContent(File file) {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader br = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
        } catch (Exception e) {
            log.error("Read file({}) content failed", file.getAbsolutePath());
        }
        return builder.toString();
    }

}
