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

    public static void main(String[] args) {


        int time_ex=30;

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                if(index1==time_ex){



                    System.out.println("\n\n\nArchitecture Distrubiée\nTemps d'exuction: "+time_ex+" seconcds");
                            ArrayList<Integer> arrayList = howMuchNormal();
                            int x = arrayList.get(0)+arrayList.get(1);
                            System.out.println("Packets Detecters: "+x);
                            //System.out.println("Packets Classifiers: "+ManagerAgent.packetsClassified.size());
                            //ArrayList<Integer> arrayList = howMuchNormal(ManagerAgent.packetsClassified);


                    System.out.println("Normal: "+arrayList.get(0)+"\t("+(arrayList.get(0)*100/(arrayList.get(0)+arrayList.get(1)))+")%");
                            System.out.println("Anomaly: "+arrayList.get(1)+"\t("+(arrayList.get(1)*100/(arrayList.get(0)+arrayList.get(1)))+")%");

                    try {
                        PlatformPara.containerController.getPlatformController().kill();
                    } catch (ControllerException e) {
                        e.printStackTrace();
                    }


                    try {
                        getSummary();
                        System.exit(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //System.exit(-1);



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








            /*
            Timer timer = new Timer();
        AgentController finalAgentController = agentController;
        timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    ACLMessage msg = ManagerAgent.aclMessages.get(0);

                    for(int i=1;i<ManagerAgent.aclMessages.size();i++){
                        System.out.println("MessageID"+i+":\nSender:"+msg.getSender()+"\n-Receivers:"+msg.getAllReceiver()+"\nContent:"+msg.getContent()+"\n");
                    }
                }
            },15000);

*/










    public static String meth(){

        //String x="GA:N_1,SS_A,SSN_A,SA_A,NP_120,NA_60|"+"N_2,SS_A,SSN_A,SA_A,NP_130,NA_60|";
        String message ="GA:";




        for(int i=0;i<ManagerAgent.containers.size();i++){
            MessageContainer messageContainer = ManagerAgent.containers.get(i).getThemAll();
            message+=messageContainer.toSend()+"|";
        }

        return message;
    }

    public static  String getMessages(){

        String x = "GM:";

        for(int i=0;i<ManagerAgent.messages.size();i++){
            Message message = ManagerAgent.messages.get(i);
            Message2 message2 = new Message2();
            message2.setSender(message.getSender());
            message2.setReciever(message.getReciever());
            message2.setContent(message.getContent());
            message2.setTime(""+message.getTime().getHour()+":"+message.getTime().getMinute()+":"+message.getTime().getSecond());
            String word=message2.getSender()+","+message2.getReciever()+","+message2.getContent()+","+message2.getTime()+",";

            x+=word+"|";

        }

        return x;
    }

    public static ArrayList<Integer> howMuchNormal(){


        ArrayList<Integer> arrayList1 = new ArrayList<>();
        arrayList1.add(0);
        arrayList1.add(0);
        for(int i=0;i<ManagerAgent.containers.size();i++){
            ArrayList<PacketDetected> arrayList = ManagerAgent.containers.get(i).getPacketClassified();

            for(int j=0;j<arrayList.size();j++){
                if(arrayList.get(j).getCategory().equals("Normal")){
                    arrayList1.set(0,arrayList1.get(0)+1);
                }else{
                    arrayList1.set(1,arrayList1.get(1)+1);
                }
            }
        }



        return arrayList1;
    }

    public static ArrayList<Integer> howMuchNormal2(Container container){


        ArrayList<Integer> arrayList1 = new ArrayList<>();
        arrayList1.add(0);
        arrayList1.add(0);

        for(int i=0;i<container.getPacketClassified().size();i++){
            if(container.getPacketClassified().get(i).getCategory().equals("Normal")){
                arrayList1.set(0,arrayList1.get(0)+1);
            }else{
                arrayList1.set(1,arrayList1.get(1)+1);
            }
        }


        return arrayList1;
    }




    public static void getSummary() throws Exception{
        String fileName="C:\\Users\\pc\\Desktop\\IDSData+type\\Messages"+PlatformPara.startTime+".txt";

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        String time_work="Started: "+PlatformPara.startTime+"\nEnded: "+dtf.format(LocalDateTime.now());
        String container="Number of containers: "+ManagerAgent.containers.size();
        String details_of_containers="";

        for(int i=0;i<ManagerAgent.containers.size();i++){
            String de="Container "+i+": ";
            ArrayList<Integer> arrayList = howMuchNormal2(ManagerAgent.containers.get(i));
            int x= arrayList.get(0)+arrayList.get(1);
            de+="\nTotale packets: "+(x);
            de+="\nNormal: "+arrayList.get(0)+" ("+(arrayList.get(0)*100/x)+"%)";
            de+="\nAnomalie: "+arrayList.get(1)+" ("+(arrayList.get(1)*100/x)+"%)";

            details_of_containers+="\n"+de+"\n--------------------------------------";

        }

        String str = "\n"+time_work+"\n"+container+"\n"+details_of_containers;



        try {

            File myObj = new File(fileName);
            if (myObj.createNewFile()) {
                //System.out.println("File created: " + myObj.getName());
            } else {
                //System.out.println("File already exists.");
            }

            FileWriter fileWritter = new FileWriter(fileName,true);
            BufferedWriter bw = new BufferedWriter(fileWritter);
            bw.write("\n"+str);
            bw.close();

            //System.out.println("Successfully wrote to the file.");


        } catch (IOException e) {
            //System.out.println("An error occurred.");
            e.printStackTrace();
        }




    }

    }



