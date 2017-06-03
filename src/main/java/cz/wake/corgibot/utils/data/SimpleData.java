package cz.wake.corgibot.utils.data;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SimpleData implements DataManager<List<String>> {

    public static final Pattern NEWLINE_PATTERN = Pattern.compile("\\r\\n?|\\r?\\n");
    private final List<String> data = new ArrayList<>();
    private final Path path;

    public SimpleData(String file) {
        this.path = Paths.get(file);
        if (!this.path.toFile().exists()) {
            System.out.println("Could not find config file at " + this.path.toFile().getAbsolutePath() + ", creating a new one...");
            try {
                if (this.path.toFile().createNewFile()) {
                    System.out.println("Generated new config file at " + this.path.toFile().getAbsolutePath() + ".");
                    FileIOUtils.write(this.path, this.data.stream().collect(Collectors.joining()));
                    System.out.println("Please, fill the file with valid properties.");
                } else {
                    System.out.println("Could not create config file at " + file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Collections.addAll(data, NEWLINE_PATTERN.split(FileIOUtils.read(this.path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        data.removeIf(s -> s.startsWith("//"));
    }

    @Override
    public List<String> get() {
        return data;
    }

    @Override
    public void save() {
        try {
            FileIOUtils.write(path, this.data.stream().collect(Collectors.joining("\n")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
