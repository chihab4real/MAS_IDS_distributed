import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;

public class ManagerAgent extends Agent  {
    public static int analysornumber = 1;
    public static ArrayList<Message> messages;
    public static ArrayList<Container> containers=new ArrayList<>();
    public static ArrayList<PacketSolved> packetSolveds=new ArrayList<>();
    public static int classREady=0;
    public static boolean needToUpadte=false;
    public static boolean end=false;


    @Override
    protected void setup() {
        messages = new ArrayList<>();

        //System.out.println("I'm Manager Agent");




        addBehaviour(new TickerBehaviour(this,600000) {
            //behaviour: sending a check message every specific time
            @Override
            protected void onTick() {
                if(classREady==1){
                    InitialzeAllContainerInf();
                    for(int i=0;i<containers.size();i++){
                        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                        msg.setContent("Check");
                        AID dest = new AID("SubManagerAgent_Container"+(i+1),AID.ISLOCALNAME);
                        msg.addReceiver(dest);
                        send(msg);

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        try {
                            //PlatformPara.NotifyMessages(new Message(msg.getSender().getLocalName(),"SubManagerAgent_Container"+(i+1),msg.getContent()),0);
                            ManagerAgent.addMessage(new Message(msg.getSender().getLocalName(),"SubManagerAgent_Container"+(i+1),msg.getContent()));
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    }

                    //iniz();
                    Verify();
                }

            }
        });

        addBehaviour(new CyclicBehaviour() {

            @Override
            public void action() {



                if(needToUpadte){
                    //send update order to classifagent
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.setContent("update");
                    AID dest = null;
                    dest = new AID("ClassifAgent", AID.ISLOCALNAME);
                    msg.addReceiver(dest);
                    send(msg);
                    try {
                        ManagerAgent.addMessage(new Message(msg.getSender().getLocalName(),"ClassifAgent",msg.getContent()));
                        //PlatformPara.NotifyMessages(new Message(msg.getSender().getLocalName(),"ManagerAgent",msg.getContent()),0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    needToUpadte=false;
                }


                ACLMessage message = receive();
                if (message != null) {

                    if(message.getContent().equals("ClassifReady")){

                        addBehaviour(new CreateContainers());
                    }

                    if(message.getContent().contains("Check50_A")){
                        //receiving message: that a container reached 50 anomaly packets
                        String cid = message.getContent().replace("Check50_A","");
                        updateNetworkState();
                        int state = CheckNetworkState();


                        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                        msg.setContent("ADT_"+String.valueOf(state));
                        AID dest = null;
                        dest = new AID("AnalysorAgent_Container"+cid, AID.ISLOCALNAME);
                        msg.addReceiver(dest);
                        System.out.println("send");
                        send(msg);

                        Message messageListe;
                        messageListe = new Message(msg.getSender().getLocalName(), "AnalysorAgent_Container"+cid, msg.getContent());
                        ManagerAgent.addMessage(messageListe);

                        block();

                    }
                }


            }
        });


    }

    public static void addMessage(Message message){
        messages.add(message);

        try {
            PlatformPara.NotifyMessages(message,messages.size()-1);
        } catch (Exception e) {
            e.printStackTrace();
        }




    }


    public void updateNetworkState(){
        for(int i=0;i<containers.size();i++){
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent("netst");
            AID dest = null;
            dest = new AID("AnalysorAgent_Container"+String.valueOf(i+1), AID.ISLOCALNAME);
            msg.addReceiver(dest);
            send(msg);

            Message messageListe;
            messageListe = new Message(msg.getSender().getLocalName(), "AnalysorAgent_Container"+String.valueOf(i+1), msg.getContent());
            ManagerAgent.addMessage(messageListe);
        }
    }

    public int CheckNetworkState(){
        int count=0;
        for(int i=0;i<containers.size();i++){
            if(containers.get(i).getCuurentstate()>=5){
                count+=1;
            }
        }

        return count;
    }

    void InitialzeAllContainerInf(){
        for(int i=0;i<containers.size();i++){
            containers.get(i).setInformed(false);
        }
    }

    void Verify(){
        for(int i=0;i<containers.size();i++){
            System.out.println("----------------------------------------------\nSubamanager Agent of container:"+(i+1)+"is alive: "+containers.get(i).isInformed()+"\n" +
                    "------------------------------------------------------------");
            if(!containers.get(i).isInformed()){
                //DELETE IT
            }
        }
    }




}