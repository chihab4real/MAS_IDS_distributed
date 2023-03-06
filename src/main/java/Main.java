import com.mongodb.*;
import jade.core.Profile;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Main {


    static int index1=0;
    static String code = "_"+ManagerAgent.treating_time +"_"+ManagerAgent.numberOfContainers;


    public static void main(String[] args) {


        int time_ex=300;

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if(index1==time_ex){



                    /*System.out.println("\n\n\nArchitecture Distrubiée\nTemps d'exuction: "+time_ex+" seconcds");
                            ArrayList<Integer> arrayList = howMuchNormal();
                            int x = arrayList.get(0)+arrayList.get(1);
                            System.out.println("Packets Detecters: "+x);
                            //System.out.println("Packets Classifiers: "+ManagerAgent.packetsClassified.size());
                            //ArrayList<Integer> arrayList = howMuchNormal(ManagerAgent.packetsClassified);


                    System.out.println("Normal: "+arrayList.get(0)+"\t("+(arrayList.get(0)*100/(arrayList.get(0)+arrayList.get(1)))+")%");
                            System.out.println("Anomaly: "+arrayList.get(1)+"\t("+(arrayList.get(1)*100/(arrayList.get(0)+arrayList.get(1)))+")%");*/

                    try {
                        PlatformPara.containerController.getPlatformController().kill();
                    } catch (ControllerException e) {
                        e.printStackTrace();
                    }


                    System.out.println("END");

                    try {
                        sendPackettoDB();
                        sendMessagestoDB();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //System.exit(-1);

                    System.exit(0);





                }

                index1++;

            }
        },58000,1000);


        PlatformPara.profile.setParameter(Profile.MAIN_HOST, "localhost");
        PlatformPara.profile.setParameter(Profile.GUI, "true");
        PlatformPara.containerController = PlatformPara.runtime.createMainContainer(PlatformPara.profile);
        PlatformPara.startTime = PlatformPara.methode();

        //


        AgentController agentController = null;
        try {

            agentController = PlatformPara.containerController.createNewAgent("ManagerAgent", "ManagerAgent", null);
            agentController.start();

            agentController = PlatformPara.containerController.createNewAgent("ClassifAgent", "ClassifAgent", null);
            agentController.start();


        } catch (StaleProxyException e) {
            e.printStackTrace();
        }


    }




















    public static void sendPackettoDB()throws Exception{
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB("PacketsDistri");

        DBCollection collection = database.getCollection("PacketsDetected"+code);

        int number=0;
        int number2=0;


        for(Container container:ManagerAgent.containers){
            number+=container.getAll().size();
            number2+=container.getPacketClassified().size();

            for(PacketDetected p: container.getPacketClassified()){
                DBObject dbObject = p.toDBObject();
                collection.insert(dbObject);
            }
        }

        System.out.println(number+"\n"+number2);


    }

    public static void sendMessagestoDB()throws Exception{
        MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
        DB database = mongoClient.getDB("PacketsDistri");

        DBCollection collection = database.getCollection("Messages"+code);

        System.out.println(PlatformPara.messages.size()+"\n");


        for(Message message:PlatformPara.messages){

            DBObject dbObject = message.toDBObject();
            collection.insert(dbObject);
        }


    }

    }



