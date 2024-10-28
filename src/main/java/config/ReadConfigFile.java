package config;

import java.io.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import config.BeanDeserializer.BeanDeserializerModifierWithValidation;

public class ReadConfigFile {

    public static Gson gson;

    public ConfigFile getConfigFile(String pathToConfig) throws FileNotFoundException, IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        File file = new File(pathToConfig);
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        byte[] contents = new byte[1024];
        int bytesRead;
        StringBuilder fileContent = new StringBuilder();

        while ((bytesRead = bufferedInputStream.read(contents)) != -1) {
            fileContent.append(new String(contents, 0, bytesRead));
        }
        String fileInput = String.valueOf(fileContent);
        ObjectMapper objectMapper = getObjectMapper();
        return objectMapper.readValue(fileInput, ConfigFile.class);

    }

    private ObjectMapper getObjectMapper() {
        SimpleModule validationModule = new SimpleModule();
        validationModule.setDeserializerModifier(new BeanDeserializerModifierWithValidation());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(validationModule);
        return mapper;

    }

    public static Gson getGson() {
        return gson;
    }

    public static void setGson(Gson gson) {
        ReadConfigFile.gson = gson;
    }
}
