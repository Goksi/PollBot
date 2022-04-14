package tech.goksi.pollbot.config;

import org.bspfsystems.yamlconfiguration.file.FileConfiguration;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("ConstantConditions")
public class Config {
    private FileConfiguration config = null;
    private File configFile = null;
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    public FileConfiguration getValues(){
        if(config == null){
            reloadConfig();
        }
        return config;
    }

    public void reloadConfig(){
        if(configFile == null){
            configFile = new File("config.yml");
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        URL url = this.getClass().getClassLoader().getResource("config.yml");
        URLConnection urlConnection = null;
        try {
            assert url != null;
            urlConnection = url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert urlConnection != null;
        urlConnection.setUseCaches(false);
        InputStream is = null;
        try{
            is = urlConnection.getInputStream();
        }catch (IOException e){
            e.printStackTrace();
        }

        assert is != null;
        Reader defConfigStream = new InputStreamReader(is, StandardCharsets.UTF_8);
        if(defConfigStream != null){
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            config.setDefaults(defConfig);
        }
    }

    public void initConfig(){
        if(configFile == null) configFile = new File("config.yml");
        if(!configFile.exists()){
            InputStream in  = this.getClass().getClassLoader().getResourceAsStream("config.yml");

            File outFile = new File("config.yml");
            try{
                if(!outFile.exists()){
                    OutputStream out = new FileOutputStream(outFile);
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len=in.read(buffer)) > 0){
                        out.write(buffer, 0, len);
                    }
                    out.close();
                    in.close();
                }

            }catch (IOException e){
                logger.error("Error while writing config file", e);
            }
        }
    }
}
