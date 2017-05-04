import edu.duke.*;
import java.util.*;

public class GladLibMap {
    private HashMap<String,ArrayList<String>> myMap;
    
    private ArrayList<String> seenWords;
    private ArrayList<String> labelsUsed;
    
    private Random myRandom;
    
    private static String dataSourceURL = "http://dukelearntoprogram.com/course3/data";
    private static String dataSourceDirectory = "data";
    
    public GladLibMap(){
        myMap = new HashMap<String,ArrayList<String>>();
        labelsUsed = new ArrayList<String>();
        
        initializeFromSource(dataSourceDirectory);
        myRandom = new Random();
    }
    
    public GladLibMap(String source){
        myMap = new HashMap<String,ArrayList<String>>();
        labelsUsed = new ArrayList<String>();
        
        initializeFromSource(source);
        myRandom = new Random();
    }
    
    private void initializeFromSource(String source) {
        FileResource fr = new FileResource("properties.txt");
        
        for ( String s : fr.lines() ) {
           String key = s.substring(0, s.indexOf(':'));
           String file = s.substring( s.indexOf(':') + 1);
           
           ArrayList<String> list = readIt(file);
           myMap.put(key, list);
           
        }
        
        seenWords = new ArrayList<String>();
    }
    
    private void print (String msg) {
        System.out.println(msg);
    }
    
    private String randomFrom(ArrayList<String> source){
        int index = myRandom.nextInt(source.size());
        return source.get(index);
    }
    
    private String getSubstitute(String label) {
        if (label.equals("number")){
            return ""+myRandom.nextInt(50)+5;
        }
        
        if (!labelsUsed.contains(label)) {
            labelsUsed.add(label);
        }
        
        return randomFrom(myMap.get(label));
    }
    
    private String processWord(String w){
        int first = w.indexOf("<");
        int last = w.indexOf(">",first);
        if (first == -1 || last == -1){
            return w;
        }
        String prefix = w.substring(0,first);
        String suffix = w.substring(last+1);
        String sub = "";
        do {
            sub = getSubstitute(w.substring(first+1,last));
        } while (seenWords.contains(sub));
        
        seenWords.add(sub);
        
        return prefix+sub+suffix;
    }
    
    private void printOut(String s, int lineWidth){
        int charsWritten = 0;
        for(String w : s.split("\\s+")){
            if (charsWritten + w.length() > lineWidth){
                System.out.println();
                charsWritten = 0;
            }
            System.out.print(w+" ");
            charsWritten += w.length() + 1;
        }
    }
    
    private String fromTemplate(String source){
        String story = "";
        if (source.startsWith("http")) {
            URLResource resource = new URLResource(source);
            for(String word : resource.words()){
                story = story + processWord(word) + " ";
            }
        }
        else {
            FileResource resource = new FileResource(source);
            for(String word : resource.words()){
                story = story + processWord(word) + " ";
            }
        }
        return story;
    }
    
    private ArrayList<String> readIt(String source){
        ArrayList<String> list = new ArrayList<String>();
        if (source.startsWith("http")) {
            URLResource resource = new URLResource(source);
            for(String line : resource.lines()){
                list.add(line);
            }
        }
        else {
            FileResource resource = new FileResource(source);
            for(String line : resource.lines()){
                list.add(line);
            }
        }
        return list;
    }
    
    private int totalWordsInMap() {
        int total = 0;
        
        for (String s : myMap.keySet()) {
            
            ArrayList<String> list = myMap.get(s);
            total += list.size();
        }
        
        return total;
    }
    
    private int totalWordsConsidered() {
        int considered = 0;
        
        for (String s : labelsUsed) {
            ArrayList<String> list = myMap.get(s);
            
            for( String word : list ){
                considered++;
            }
        }
        return considered;
    }
    
    public void makeStory(){
        seenWords.clear();
        labelsUsed.clear();
        
        print("\n");
        String story = fromTemplate("data/madtemplate3.txt");
        printOut(story, 60);
        print("\n\nWords Seen: " + seenWords.size());
        print("There are a total of " + totalWordsInMap() + " words in the map, of which " + 
            totalWordsConsidered() + " were considered.");
    }
}