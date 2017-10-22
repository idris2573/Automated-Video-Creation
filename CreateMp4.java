import java.io.*;
import java.util.ArrayList;

/**
 * Created by Idris on 31/08/2017.
 */
public class CreateMp4 {

    boolean fileComplete;
    String filename;

    String command1;

    File mp3Path;
    File finalMp4;

    ArrayList<String> pngList;
    String[] list;
    File[] completed;
    File[] uploaded;
    ArrayList completedList;
    ReadVideoInfo readVideoInfo = new ReadVideoInfo();
    ReadFile readActive = new ReadFile();

    String mainString;
    String statusString;

    boolean runProgram = true;

    String completedString;


    public static void main(String[]args) throws Exception{
        CreateMp4 createMp4 = new CreateMp4();
        createMp4.run();
    }

    void run() throws Exception{

        boolean running = true;
        boolean waiting = true;


        while(running) {
            if(runProgram) {
                //consoleString();


                checkComplete();
                readVideoInfo.read("files/Dictionary.csv");
                int i = 0;

                while (i < list.length && runProgram) {


                    try {
                        initalize(i);
                        readActive.read("files/active.txt");

                        if (!filename.equals("delete")) {

                            System.out.println("TESTING2");

                            if (filename.equals(readActive.textLine.get(0))) {

                                System.out.println("Is active file...\n");
                                statusString = "Is active file...\n";

                            } else if (!completedList.contains(filename) && !filename.equals(readActive.textLine.get(0))) {
                                getCmd();
                                step1();
                                System.out.println("COMPLETE\n");
                                mainString = ("STOPPED OR COMPLETE\n");

                                if (i >= list.length) {
                                    waiting = true;
                                }

                            } else {
                                System.out.println("Waiting...\n");
                                statusString = "Waiting...\n";
                            }
                        }

                    } catch (Exception e) {
                        System.out.println("Failed... Skipping..." + "\n" + e.getMessage());
                    }
                    i++;

                    //getConsoleString();
                }

                if (waiting) {
                    System.out.println("Waiting...");
                    statusString = "Waiting...";
                    waiting = false;
                }
            }
        }

    }


//--------------------------------------------INITIALIZE------------------------------------------------------//

    void checkComplete(){

        new File("files/completed/").mkdirs();
        new File("files/completed/uploaded").mkdirs();

        list = new File("files/png").list();
        completed = new File("files/completed/").listFiles();
        uploaded = new File("files/completed/uploaded").listFiles();
        completedList = new ArrayList<>();
        pngList = new ArrayList<>();

        for(int i = 0; i < completed.length; i++){
            String tempCompleted = completed[i].getName().replace(".mp4","");
            completedList.add(tempCompleted);
        }

        for(int i = 0; i < uploaded.length; i++){
            String tempCompleted = uploaded[i].getName().replace(".mp4","");
            completedList.add(tempCompleted);
        }

        for(int i = 0; i < completedList.size(); i++) {
            //System.out.println("completed list : " + completedList.get(i));
        }

        for(int i = 0; i < list.length; i++){
            pngList.add(list[i]);
        }

      
        for (int i = 0; i < pngList.size(); i++) {
            for (int j = 0; j < completedList.size(); j++) {
                if (completedList.get(j).equals(pngList.get(i))) {
                    pngList.remove(i);

                    if(pngList.get(i)==pngList.get(pngList.size()-1))
                        i = pngList.size();
                }
            }
        }
       
        completedList.clear();
        //pngList.clear();

        //System.out.println();
    }

    void initalize(int count){


        filename = pngList.get(count).replace(".mp4","");


        if(!filename.equals("delete")){
            System.out.println(count + " filename : " + filename);
            mainString = count + " filename : " + filename;

        }

        mp3Path = new File("files/audio2/" + filename + ".wav");
        finalMp4 = new File("files/completed/" + filename + ".mp4");


        fileComplete = false;

    }

    void getCmd()throws Exception{

        String open = "cmd.exe /c start /min ";
        String open2 = "cmd.exe /c start cmd /k ";

        command1 = open + "ffmpeg -r 24 -f image2 -s 1280x720 -i \"files/png/" + filename + "/image-%04d.png\" -i \"" + mp3Path + "\" -shortest \"" + finalMp4 + "\"";

    }


    //--------------------------------------------STEPS------------------------------------------------------//

    void step1() throws Exception{
        if(!finalMp4.exists()) {
            Process p1 = Runtime.getRuntime().exec(command1);
            statusString = "Rendering...";
            checkRunning();

            Main.addmp4 = true;
            completedString = completedString + "\n" + filename;

            //System.out.println("step 1 complete");
            new File("files/png/delete").mkdirs();
            new File("files/png/" + filename).renameTo(new File("files/png/delete/" + filename));
            new File("files/png/delete/" + filename).delete();

        }
    }


    //--------------------------------------------EXTRA------------------------------------------------------//

    void checkRunning(){
        Boolean running = true;
        ArrayList<String> ffmpegTest = new ArrayList<>();

        while (running) {

            String line;
            try {
                Process proc = Runtime.getRuntime().exec("wmic.exe");

                BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                OutputStreamWriter oStream = new OutputStreamWriter(proc.getOutputStream());
                oStream.write("process where name='ffmpeg.exe'");
                oStream.flush();
                oStream.close();

                while ((line = input.readLine()) != null) {


                    if (line.contains("ffmpeg")) {
                        ffmpegTest.add("yes");
                        //System.out.println(ffmpegTest.get(ffmpegTest.size()-1));
                    }

                }
                input.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            if (!ffmpegTest.contains("yes")) {
                running = false;
                fileComplete = true;
                statusString = "Completed...";
            }

            ffmpegTest.clear();
        }

        //System.out.println("Complete");

    }

}
