import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.*;

import java.util.Random;
import java.util.Timer;


public class SnifferAgent extends Agent {



    @Override
    protected void setup() {



        Instances test = null;

        try {
            test = new DataSource("KDDTest.arff").getDataSet();
            test.setClassIndex(test.numAttributes()-1);



        } catch (Exception e) {
            e.printStackTrace();
        }

        String containerID = getMyID(getAID().getLocalName());









        Instances finalTest = test;
        addBehaviour(new TickerBehaviour(this,new Random().nextInt((900 - 100) + 1) + 100) {
            @Override
            protected void onTick() {

                PacketSniffer packetSniffer = new PacketSniffer(finalTest.get(new Random().nextInt(finalTest.size()-1)),false);




                ManagerAgent.containers.get(Integer.parseInt(containerID)-1).getPacketsDetected().add(packetSniffer);
                ManagerAgent.containers.get(Integer.parseInt(containerID)-1).getAll().add(packetSniffer);



                reset(new Random().nextInt((900 - 100) + 1) + 100);
            }
        });

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {

                ACLMessage message = receive();
                if(message!=null){
                    if(message.getContent().equals("Check")){
                        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                        msg.setContent("Check_OK");
                        AID dest = null;
                        dest = new AID("SubManagerAgent_Container"+String.valueOf(containerID), AID.ISLOCALNAME);
                        msg.addReceiver(dest);
                        send(msg);
                        try {
                            PlatformPara.messages.add(new Message(msg.getSender().getLocalName(),"SubManagerAgent_Container"+String.valueOf(containerID),msg.getContent()));
                            Thread.sleep(ManagerAgent.treating_time);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });







        /*addBehaviour(new TickerBehaviour(this, 5000) {
            @Override
            protected void onTick() {



               Instances test = null;
                
                try {
                    test = new DataSource("KDDTest.arff").getDataSet();
                    test.setClassIndex(test.numAttributes()-1);
                  


                } catch (Exception e) {
                    e.printStackTrace();
                }


                PacketSniffer packetSniffer = new PacketSniffer(test.get(new Random().nextInt(test.size()-1)),false);




                ManagerAgent.containers.get(Integer.parseInt(containerID)-1).getPacketsDetected().add(packetSniffer);
                ManagerAgent.containers.get(Integer.parseInt(containerID)-1).getAll().add(packetSniffer);




                /*if(!arrayList.contains(packetSniffer.getInstance())){
                    arrayList.add(packetSniffer.getInstance());
                    System.out.println("Size :"+arrayList.size());
                }
                System.out.println(arrayList.contains(packetSniffer.getInstance()));








            }
        });*/



/*

       addBehaviour(new OneShotBehaviour() {
           @Override
           public void action() {

               Random random = new Random();
               int nb_r = random.nextInt(3);

               switch (nb_r){
                   case 0://DT
                       break;

                   case 1://SVM
                       break;

                   case 2://NN
                       break;
               }


               /*
               Random random = new Random();
               int max = 10000;
               int nb_r = random.nextInt(max);

               //A la place de sniifer le traffic
               if(nb_r==5){

                   ACLMessage msg = new ACLMessage( ACLMessage.INFORM );
                   msg.setContent("code1");

                   AID dest = null;
                   try {
                       dest = new AID("SubManagerAgent_"+getAgent().getContainerController().getContainerName(),AID.ISLOCALNAME);
                   } catch (ControllerException e) {
                       e.printStackTrace();
                   }
                   msg.addReceiver(dest);

                   send(msg);

                   Message messageListe = new Message();
                   try {
                       messageListe = new Message(msg.getSender().getLocalName(),"SubManagerAgent_"+getContainerController().getContainerName(),msg.getContent());
                       ManagerAgent.messages.add(messageListe);

                   } catch (ControllerException e) {
                       e.printStackTrace();
                   }

                   block(5000);


               }

           }
       })


*/


}

    public String getMyID(String AID){
        return AID.replace("SnifferAgent_Container","");
    }

    public int Exist(PacketSniffer packetSniffer,String containerID){



        return 0;
    }







}

