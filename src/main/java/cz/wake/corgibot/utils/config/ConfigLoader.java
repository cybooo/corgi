package cz.wake.corgibot.utils.config;

import com.afollestad.ason.Ason;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.commons.text.translate.UnicodeUnescaper;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigLoader {

    /**
     * This will attempt to load the config and create it if it is not there
     *
     * @param file the file to load
     * @return the loaded config
     * @throws Exception if something goes wrong
     */
    public static Config getConfig(final File file) throws Exception {
        if (!file.exists()) {
            file.createNewFile();
            final FileWriter writer = new FileWriter(file);
            writer.write("{}");
            writer.close();
        }
        return new MainConfig(file);
    }

    public static class MainConfig extends Config {

        private final File configFile;

        MainConfig(final File file) throws Exception {
            super(null, new Ason(Files.asCharSource(file, Charsets.UTF_8).read()));
            this.configFile = file;
        }

        @Override
        public File getConfigFile() {
            return this.configFile;
        }

        @Override
        public void save() throws Exception {
            try {
                final BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(new FileOutputStream(this.configFile), StandardCharsets.UTF_8));
                new UnicodeUnescaper().translate(
                        this.config.toString(4), writer);
                writer.close();
            } catch (final IOException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }
}