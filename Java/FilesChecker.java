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

    public String[] checkForFile(String searchTerm, int mode){ //mode 0 partial search, mode 1 exact search
        ArrayList<String> files = this.getAllFiles();
        String file = "";
        if(mode == 0){
            if(View.debug) System.out.println("Looking in files for a partial match with \"" + searchTerm + "\"");
            for(String s : files){
                if(s.contains(searchTerm)){
                    file = s;
                    break;
                }
            }
        }
        else if (mode == 1){
            if(View.debug) System.out.println("Looking in files for an exact match with \"" + searchTerm + "\"");
            for(String s : files){
                if(s.equals(searchTerm)){
                    file = s;
                    break;
                }
            }
        }

        if(file.length() > 0){
            Path path = Path.of(dataFolder + file);
            long bytes;
            try {
                bytes = Files.size(path);
                if(View.debug) System.out.println("File found. Name: \"" + file + "\" Size: " + bytes + "bytes");
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
