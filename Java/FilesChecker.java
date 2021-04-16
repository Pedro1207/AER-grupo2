import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class FilesChecker {

    private final String dataFolder;

    public FilesChecker(String dataFolder) {
        this.dataFolder = dataFolder;
    }

    public String[] checkForFile(String searchTerm){
        ArrayList<String> files = this.getAllFiles();

        String file = "";
        for(String s : files){
            if(s.contains(searchTerm)){
                file = s;
                break;
            }
        }

        if(file.length() > 0){
            Path path = Path.of(dataFolder + file);
            long bytes;
            try {
                bytes = Files.size(path);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return new String[]{file, String.valueOf(bytes)};

        }else{
            return null;
        }

    }


    private ArrayList<String> getAllFiles(){
        File[] files = new File(dataFolder).listFiles();
        ArrayList<String> results = new ArrayList<>();

        if(files == null) return results;
        for(File file : files){
            if(file.isFile()){
                results.add(file.getName());
            }
        }

        return results;
    }
}
