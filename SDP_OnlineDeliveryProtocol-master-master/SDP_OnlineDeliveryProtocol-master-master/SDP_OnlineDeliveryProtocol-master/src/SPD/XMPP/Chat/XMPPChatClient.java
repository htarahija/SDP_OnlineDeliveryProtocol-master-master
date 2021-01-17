package SPD.XMPP.Chat;

import FSM.IFSM;
import FSM.FSM;
import MessageTemplate.Message;
import FSM.TcpTransportClient;
import FSM.Dispatcher;
import FSM.IMessage;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class XMPPChatClient extends FSM implements IFSM {
    static String TOKEN = "123";
    static int IDLE = 0;
    static int LOGIN = 1;
    static int WAITING_FOR_RESPONSE = 4;
    static int REGISTRATION = 5;
    static int LOGIN_AFTER_REGISTRATION = 6;
    static int LOGINED_USER = 8;
    static int VIEW_PRODUCTS = 10;
    static int VIEW_SORTED_PRODUCTS = 13;
    static int VIEW_AVAILABLE_PRODUCTS = 14;
    static int ORDER = 15;
    static int WRITE_FEEDBACK = 16;
    static int VIEW_FEEDBACK = 17;
    public XMPPChatClient(int id) {
        super(id);
    }
    @Override
    public void init() {
        setState(IDLE);

        // REGISTRACIJA I LOGIN

        addTransition(IDLE, new Message(Message.Types.RESOLVE_DOMAIN_NAME), "resolveDomain");
        addTransition(LOGIN, new Message(Message.Types.LOGIN_REQUEST), "loginRequest");
        addTransition(WAITING_FOR_RESPONSE, new Message(Message.Types.LOGIN_SUCCESSFUL_USER), "loginSuccessUser");
        addTransition(WAITING_FOR_RESPONSE, new Message(Message.Types.REGISTRATION_REQUIRED), "registrationRequired");
        addTransition(REGISTRATION, new Message(Message.Types.REGISTRATION_REQUEST), "registrationRequest");
        addTransition(LOGIN_AFTER_REGISTRATION, new Message(Message.Types.LOGIN_AFTER_REG), "loginAfterRequest");

        // ITEMS

        addTransition(VIEW_PRODUCTS, new Message(Message.Types.ASKING_FOR_SORTED_ITEMS), "askingForSortedItems");
        addTransition(VIEW_PRODUCTS, new Message(Message.Types.ASKING_FOR_AVAILABLE_ITEMS), "askingForAvailableItems");
        addTransition(VIEW_PRODUCTS, new Message(Message.Types.ASKING_FOR_AVAILABLE_ITEMS), "askingForFeedbacks");
        addTransition(VIEW_SORTED_PRODUCTS, new Message(Message.Types.SENDING_SORTED_ITEMS), "reviewingSortedItems");
        addTransition(VIEW_AVAILABLE_PRODUCTS, new Message(Message.Types.SENDING_AVAILABLE_ITEMS), "reviewingAvailableItems");
        addTransition(VIEW_FEEDBACK, new Message(Message.Types.SENDING_FEEDBACK), "reviewingFeedbacks");
        addTransition(ORDER, new Message(Message.Types.ORDER_ITEM), "orderAvailableItem");
        addTransition(WRITE_FEEDBACK, new Message(Message.Types.FEEDBACK), "writeFeedback");

    }

    //
    // //
    // //   REGISTRACIJA I LOGIN
    // //
    //

    public void resolveDomain(IMessage message){
        Message msg = (Message)message;
        System.out.println("Resolving: " + msg.getParam(Message.Params.DOMAIN));
        //resolve domain, implement this please!
        Message tcpMSG = new Message(5555);
        tcpMSG.addParam(Message.Params.IP, "127.0.0.1");
        sendMessage(tcpMSG);
        System.out.println("Resolved!");
        setState(LOGIN);
    }

    public void loginRequest(IMessage message){
        Message msg = (Message) message;
        msg.setToId(5);
        msg.setMessageId(Message.Types.LOGIN_REQUEST);
        sendMessage(msg);
        System.out.println("Login...");
        System.out.println(msg.getParam("username") + msg.getParam("email") + msg.getParam("password") + msg.getParam("role"));
        setState(WAITING_FOR_RESPONSE);
    }

    public void loginSuccessUser(IMessage message){
        System.out.println("User login!");
        setState(LOGINED_USER);
    }
    public void registrationRequired(IMessage message){
        System.out.println("Registration required");
        setState(REGISTRATION);
    }
    public void registrationRequest(IMessage message){
        Message msg = (Message) message;
        msg.setToId(5);
        msg.setMessageId(Message.Types.REGISTRATION_REQUEST);
        sendMessage(msg);
        System.out.println("Registration...");
        System.out.println(msg.getParam("username") + msg.getParam("email") + msg.getParam("password") + msg.getParam("role"));
        setState(LOGIN_AFTER_REGISTRATION);
    }
    public void loginAfterRequest(IMessage message){
        Message msg = (Message) message;
        msg.setToId(5);
        msg.setMessageId(Message.Types.LOGIN_AFTER_REG);
        sendMessage(msg);
        System.out.println("Registred.");
        System.out.println("Login after registration...");
        System.out.println(msg.getParam("username") + msg.getParam("email") + msg.getParam("password") + msg.getParam("role"));
        setState(VIEW_PRODUCTS);
    }


    //
    // //
    // // ITEMS
    // //
    //


    public void askingForSortedItems(IMessage message){
        Message msg = (Message) message;
        msg.setToId(5);
        msg.setMessageId(Message.Types.ASKING_FOR_SORTED_ITEMS);
        sendMessage(msg);
        System.out.println("Sending request for sorted items...");
        setState(VIEW_SORTED_PRODUCTS);
    }
    public void askingForAvailableItems(IMessage message){
        Message msg = (Message) message;
        msg.setToId(5);
        msg.setMessageId(Message.Types.ASKING_FOR_AVAILABLE_ITEMS);
        sendMessage(msg);
        System.out.println("Sending request for available items...");
        setState(VIEW_AVAILABLE_PRODUCTS);
    }

    public void reviewingSortedItems(IMessage message){
        Message msg = (Message) message;
        ArrayList<Items> items = new ArrayList<Items>();
        items = (ArrayList<Items>)msg.getParam(Message.Params.ITEMS, true);
        System.out.println("Sorted item list:");
        items.forEach(items1 -> System.out.println(items1));
        setState(VIEW_PRODUCTS);
    }
    public void reviewingAvailableItems(IMessage message){
        Message msg = (Message) message;
        if(msg.getParam(Message.Params.ORDER).equals("true")){
            setState(ORDER);
        }
        ArrayList<Items> items = new ArrayList<Items>();
        items = (ArrayList<Items>)msg.getParam(Message.Params.ITEMS, true);
        System.out.println("Available item list:");
        items.forEach(items1 -> System.out.println(items1));
        setState(VIEW_PRODUCTS);
    }

    public void orderAvailableItem(IMessage message){
        Message msg = (Message) message;
        msg.setToId(5);
        System.out.println("Naruči item: ");
        msg.setMessageId(Message.Types.ORDER_ITEM);
        Scanner input = new Scanner(System.in);
        String item_name = input.nextLine();
        Integer item_count = input.nextInt();
        Items ordering_item = new Items(item_name,item_count);
        msg.addParam(Message.Params.ITEMS, ordering_item);
        sendMessage(msg);
        setState(WRITE_FEEDBACK);
    }

    public void writeFeedback(IMessage message){
        Message msg = (Message) message;
        msg.setToId(5);
        msg.setMessageId(Message.Types.FEEDBACK);
        System.out.println("Slanje feedbacka");
        sendMessage(msg);
        setState(VIEW_PRODUCTS);
    }

    static int SERVER_PORT = 9999;
    static String SERVER_URL = "";
    static String SERVER_IP = "";
    public static void main(String[] args) throws Exception{
        // write your code here
        //client
        XMPPChatClient XMPPChatClientFSM = new XMPPChatClient(0);
        TcpTransportClient tcpFSM = new TcpTransportClient(5);
        tcpFSM.setServerPort(SERVER_PORT);
        tcpFSM.setReceiver(XMPPChatClientFSM);

        Dispatcher dis = new Dispatcher(false);
        dis.addFSM(XMPPChatClientFSM);
        dis.addFSM(tcpFSM);
        dis.start();


        Scanner input = new Scanner(System.in);

        String username = "";
        System.out.println("Enter URI of server in format username:port@host.ba");
        /*do{
            String temp = input.nextLine();
            if(temp.split(":").length != 2 || (temp.split(":")[1].split("@")).length != 2){
                System.out.println("Bad input, please try again!");
            }else {
                username = temp.split(":")[0];
                String port = temp.replace(username + ":","").split("@")[0];
                try{
                    SERVER_PORT = Integer.parseInt(port);
                    SERVER_URL = temp.replace(username + ":","").split("@")[1];
                    break;
                }catch(Exception e){
                    System.out.println("Bad port, please try again!");
                }
            }
        }while(true);*/

        SERVER_PORT = 9999;
        SERVER_URL = "www.olx.ba";
        System.out.println("User: " + username);
        System.out.println("Host: " + SERVER_URL + ":" + SERVER_PORT);
        tcpFSM.setServerPort(SERVER_PORT);


        Message tempMsg = new Message(Message.Types.RESOLVE_DOMAIN_NAME);
        tempMsg.setToId(0);
        tempMsg.addParam(Message.Params.DOMAIN, SERVER_URL);
        dis.addMessage(tempMsg);

        tempMsg = new Message(Message.Types.REGISTER_TO_SERVER);
        tempMsg.setToId(0);
        dis.addMessage(tempMsg);

        Thread.sleep(1000);

        tempMsg = new Message(Message.Types.LOGIN_REQUEST);
        tempMsg.setToId(0);
        tempMsg.addParam(Message.Params.USERNAME, "reha");
        tempMsg.addParam(Message.Params.PASSWORD, "123");
        tempMsg.addParam(Message.Params.EMAIL, "a@a.a");
        tempMsg.addParam(Message.Params.ROLE, "adminn");
        dis.addMessage(tempMsg);

        Thread.sleep(1000);

        tempMsg = new Message(Message.Types.REGISTRATION_REQUEST);
        tempMsg.setToId(0);
        tempMsg.addParam(Message.Params.USERNAME, "reha");
        tempMsg.addParam(Message.Params.PASSWORD, "123");
        tempMsg.addParam(Message.Params.EMAIL, "a@a.a");
        tempMsg.addParam(Message.Params.ROLE, "adminn");
        dis.addMessage(tempMsg);

        Thread.sleep(1000);

        tempMsg = new Message(Message.Types.LOGIN_AFTER_REG);
        tempMsg.setToId(0);
        tempMsg.addParam(Message.Params.USERNAME, "reha");
        tempMsg.addParam(Message.Params.PASSWORD, "123");
        tempMsg.addParam(Message.Params.EMAIL, "a@a.a");
        tempMsg.addParam(Message.Params.ROLE, "adminn");
        dis.addMessage(tempMsg);

        Thread.sleep(1000);

        tempMsg = new Message(Message.Types.ASKING_FOR_SORTED_ITEMS);
        tempMsg.setToId(0);
        dis.addMessage(tempMsg);

        Thread.sleep(1000);

        tempMsg = new Message(Message.Types.ASKING_FOR_AVAILABLE_ITEMS);
        tempMsg.setToId(0);
        dis.addMessage(tempMsg);

        Thread.sleep(1000);

        tempMsg = new Message(Message.Types.ORDER_ITEM);
        tempMsg.setToId(0);
        tempMsg.addParam(Message.Params.ORDER, "true");
        dis.addMessage(tempMsg);

        Thread.sleep(15000);

        tempMsg = new Message(Message.Types.WRITE_FEEDBACK);
        tempMsg.setToId(0);
        tempMsg.addParam(Message.Params.USERNAME, "reha");
        tempMsg.addParam(Message.Params.PASSWORD, "123");
        tempMsg.addParam(Message.Params.EMAIL, "a@a.a");
        tempMsg.addParam(Message.Params.ROLE, "adminn");
        System.out.println("Unesite Vaš feedback: ");
        Scanner input_2 = new Scanner(System.in);
        Items itemm = new Items(input_2.nextLine(), input_2.nextInt());
        tempMsg.addParam(Message.Params.ITEMS, itemm);
        tempMsg.addParam(Message.Params.FEEDBACK, "Ovo je super!");
        dis.addMessage(tempMsg);
        Thread.sleep(10000);


        while(true){


        }

    }
}
