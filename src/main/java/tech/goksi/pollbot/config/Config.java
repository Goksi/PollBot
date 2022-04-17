package tech.goksi.pollbot.config;

import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;

public class Config {
    private  YamlFile config;
    private  File configFile;
    private final Logger logger;
    public Config(){
        logger = LoggerFactory.getLogger(this.getClass().getName());
        configFile = new File("config.yml");
    }

    public void reloadConfig() throws IOException {
        configFile = new File("config.yml");
        config = new YamlFile(configFile);
        config.loadWithComments();
    }

    public void initConfig(){
        if(!configFile.exists()){
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("config.yml");
            assert is!=null;
            try{
                try (OutputStream out = Files.newOutputStream(configFile.toPath())) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = is.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                }
            }catch (IOException e){
                logger.error("Error while writing config file", e);
            }

        }
        config = new YamlFile(configFile);
        try{
            config.loadWithComments();
        }catch (InvalidConfigurationException e){
            logger.error("Wrongly formatted YAML file", e);
        }catch (IOException e){
            logger.error("Error while reading config file", e);
        }

    }

    public YamlFile getConfig() {
        return config;
    }
}
