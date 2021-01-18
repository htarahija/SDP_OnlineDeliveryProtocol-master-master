package SPD.XMPP.Chat;

import FSM.IFSM;
import FSM.FSM;
import MessageTemplate.Message;
import FSM.TcpTransportServer;
import FSM.Dispatcher;
import FSM.IMessage;

import java.time.LocalDate;
import java.util.HashMap;


import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
public class XMPPChatServer extends FSM implements IFSM {
    public XMPPChatServer(int id) {
        super(id);
    }
    ArrayList<Users> users = new ArrayList<Users>();
    ArrayList<Items> items = new ArrayList<Items>();
    ArrayList<Items> items_for_Hash = new ArrayList<Items>();

    public static HashMap<String, String> users_location = new HashMap<>(); //username, address
    public static HashMap<Users, ArrayList<Items>> items_feedback = new HashMap<>(); //users, ordered_items

    static int READY = 0;

    @Override
    public void init() {
        addTransition(READY, new Message(Message.Types.REGISTER_TO_SERVER), "onServerRegister");
        addTransition(READY, new Message(Message.Types.LOGIN_REQUEST), "checkRegisterLogin");
        addTransition(READY, new Message(Message.Types.REGISTRATION_REQUEST), "checkRegister");
        addTransition(READY, new Message(Message.Types.LOGIN_AFTER_REG), "checkRegisterLogin");
        addTransition(READY, new Message(Message.Types.ASKING_FOR_SORTED_ITEMS), "sendingSortedItems");
        addTransition(READY, new Message(Message.Types.ASKING_FOR_AVAILABLE_ITEMS), "sendingAvailableItems");
        addTransition(READY, new Message(Message.Types.ASKING_FOR_FEEDBACK), "sendingFeedback");
        addTransition(READY, new Message(Message.Types.ORDER_ITEM), "orderingItems");
        addTransition(READY, new Message(Message.Types.ADD_ITEM), "addingItems");
        addTransition(READY, new Message(Message.Types.EDIT_ITEM), "editingItems");
        addTransition(READY, new Message(Message.Types.DELETE_ITEM), "deletionItems");
        addTransition(READY, new Message(Message.Types.WRITE_FEEDBACK), "writeFeedback");
    }


    public void onServerRegister(IMessage message){
        Message msg = (Message) message;
        Message response = new Message(Message.Types.CONNECTED_SUCCESSFULL);
        response.setToAddress(msg.getFromAddress());
        System.out.println(msg.getFromAddress());
        sendMessage(response);
    }

    public void checkRegisterLogin(IMessage message){
        Message msg = (Message) message;
        Users user = new Users(msg.getParam(Message.Params.USERNAME), msg.getParam(Message.Params.EMAIL), msg.getParam(Message.Params.PASSWORD), msg.getParam(Message.Params.ROLE));
        System.out.println(user);
        System.out.println(msg.getParam("username") + msg.getParam("email"));
        boolean found = false;
        for (Users u : users) {
            if(user.equals(u)) {
                found = true;
                break;
            }
        }
        if(found){
            System.out.println("Login on server.");
            String temp_admin = "admin";
            if(temp_admin.equals(msg.getParam(Message.Params.ROLE))) {
                Message response = new Message(Message.Types.LOGIN_SUCCESSFUL_ADMIN);
                response.setToAddress(msg.getFromAddress());
                sendMessage(response);
            } else {
                Message response = new Message(Message.Types.LOGIN_SUCCESSFUL_USER);
                response.setToAddress(msg.getFromAddress());
                sendMessage(response);
            }
            System.out.println(msg.getParam(Message.Params.USERNAME) + msg.getFromAddress());
            users_location.put(msg.getParam(Message.Params.USERNAME), msg.getFromAddress());
        } else {
            Message response = new Message(Message.Types.REGISTRATION_REQUIRED);
            response.setToAddress(msg.getFromAddress());
            sendMessage(response);
            System.out.println("Registration on server.");
        }
    }


    public void checkRegister(IMessage message){
        Message msg = (Message) message;
        Users user = new Users(msg.getParam(Message.Params.USERNAME), msg.getParam(Message.Params.EMAIL), msg.getParam(Message.Params.PASSWORD), msg.getParam(Message.Params.ROLE));
        users.add(user);
        Message response = new Message(Message.Types.REGISTRATION_SUCCESSFUL);
        response.setToAddress(msg.getFromAddress());
        sendMessage(response);
    }

    //
    // //
    // // //
    // //
    //

    public void sendingSortedItems(IMessage message){
        Message msg = (Message) message;
        ArrayList<Items> items_2 = new ArrayList<Items>();
        ArrayList<Items> items_3 = new ArrayList<Items>();
        for (Items i : items) {
           items_3.add(i);
        }
        Message response = new Message(Message.Types.SENDING_AVAILABLE_ITEMS);
        for (int i = 0; i < items_3.size() - 1; i++)
        {
            for(int j = 0; j < items_3.size() - 1; j++)
            {
                if(items_3.get(i).getCOUNT() < items_3.get(j+1).getCOUNT())
                {
                    Items item = items_3.get(j+1);
                    items_3.set(j+1,items_3.get(i));
                    items_3.set(i,item);
                }
            }
        }
        response.setToAddress(msg.getFromAddress());
        response.addParam(Message.Params.ITEMS, items_3);
        sendMessage(response);
    }

    public void sendingAvailableItems(IMessage message){
        Message msg = (Message) message;
        ArrayList<Items> items_2 = new ArrayList<Items>();
        Message response = new Message(Message.Types.SENDING_SORTED_ITEMS);
        for (Items i : items) {
            if(i.getCOUNT() > 0) {
                items_2.add(i);
            }
        }
        response.setToAddress(msg.getFromAddress());
        response.addParam(Message.Params.ITEMS, items_2);
        sendMessage(response);
    }

    public void addingItems(IMessage message){
        Message msg = (Message) message;
        Items item = new Items();
        String role = msg.getParam(Message.Params.ROLE);
        if(!role.equals("admin")){
            Message response = new Message(Message.Types.UNAUTHORISED);
            response.setToAddress(msg.getFromAddress());
            sendMessage(response);
            return;
        }
        item = (Items) msg.getParam(Message.Params.ITEMS, true);
        items.add(item);
        System.out.println("ITEM ADDED");
        Message response = new Message(Message.Types.SENDING_ITEMS);
        response.setToAddress(msg.getFromAddress());
        response.addParam(Message.Params.ITEMS, items);
        sendMessage(response);
    }

    public void orderingItems(IMessage message){
        Message msg = (Message) message;
        Items itemm = new Items();
        Users user = new Users(msg.getParam(Message.Params.USERNAME), msg.getParam(Message.Params.EMAIL), msg.getParam(Message.Params.PASSWORD), msg.getParam(Message.Params.ROLE));
        itemm = (Items) msg.getParam(Message.Params.ITEMS, true);
        // ArrayList<Items> items = new ArrayList<Items>();

        // items.add(itemm);
        int s = 0;
        System.out.println(itemm);

        for (int i=0;i<items.size();i++){
            if(itemm.equals(items.get(i))){
                Integer is = items.get(i).getCOUNT() - itemm.getCOUNT();
                items.get(i).setCOUNT(is);
            }
        }
        user.setORDER_TIME(LocalDate.now());
        System.out.println(itemm);
        System.out.println(items);
        Message response = new Message(Message.Types.SENDING_ITEMS);
        response.setToAddress(msg.getFromAddress());
        response.addParam(Message.Params.ITEMS, items);
        if(items_feedback.containsKey(user)){
            System.out.println(itemm);
            System.out.println("Sadrži");
            items_feedback.get(user).add(itemm);
        } else {
            System.out.println(itemm);
            System.out.println("Nesadrži");
            items_feedback.put(user, items_for_Hash);
        }
        System.out.println(items_feedback);
        System.out.println("ITEM ORDERED");
        sendMessage(response);
    }

    public void deletionItems(IMessage message) {
        Message msg = (Message) message;
        final Items item = (Items) msg.getParam(Message.Params.ITEMS, true);
        items.removeIf(s -> s.equals(item));
        System.out.println("Item deleted :)");
        Message response = new Message(Message.Types.SENDING_ITEMS);
        response.setToAddress(msg.getFromAddress());
        items.forEach(items1 -> {
            System.out.println(items1);
        });
        response.addParam(Message.Params.ITEMS, items);
        sendMessage(response);
    }
    public void editingItems(IMessage message){
        Message msg = (Message) message;
        final Items item = (Items) msg.getParam(Message.Params.ITEMS, true);
        Message response = new Message(Message.Types.SENDING_ITEMS);
        response.setToAddress(msg.getFromAddress());
        for (Items u : items) {

            if(item.equals(u)) {
                int new_value = item.getCOUNT();
                u.setCOUNT(new_value);
                break;
            }
        }
        sendMessage(response);
    }

    public void writeFeedback(IMessage message) {
        Message msg = (Message) message;
        String feedback = msg.getParam(Message.Params.FEEDBACK);
        Items itemmm = new Items();
        itemmm = (Items) msg.getParam(Message.Params.ITEMS, true);
        Users user = new Users(msg.getParam(Message.Params.USERNAME), msg.getParam(Message.Params.EMAIL), msg.getParam(Message.Params.PASSWORD), msg.getParam(Message.Params.ROLE));
        for (Map.Entry<Users, ArrayList<Items>> entry : items_feedback.entrySet()) {
            for (Items i : entry.getValue()) {
                if (itemmm.equals(i)) {
                    i.getFEEDBACK().add(feedback);
                    break;
                }
            }
        }
    }

    public void sendingFeedback(IMessage message) {
        Message msg = (Message) message;
        String feedback = msg.getParam(Message.Params.FEEDBACK);
        Items itemmm = new Items();
        itemmm = (Items) msg.getParam(Message.Params.ITEMS, true);
        Users user = new Users(msg.getParam(Message.Params.USERNAME), msg.getParam(Message.Params.EMAIL), msg.getParam(Message.Params.PASSWORD), msg.getParam(Message.Params.ROLE));
        Message response = new Message(Message.Types.FEEDBACK);
        for (Map.Entry<Users, ArrayList<Items>> entry : items_feedback.entrySet()) {
            Users userrrr = entry.getKey();
            LocalDate trenutno = LocalDate.now();
            for (Items i : entry.getValue()) {
                if (itemmm.getITEM_NAME().equals(i.getITEM_NAME()) && trenutno.compareTo(userrrr.getORDER_TIME()) < 30) {
                    System.out.println(users_location.get(userrrr.getUSERNAME()));
                    response.addParam(Message.Params.FEEDBACK, i.getFEEDBACK());
                    response.setToAddress(users_location.get(userrrr.getUSERNAME()));
                    //System.out.println(i.getFEEDBACK());
                    //System.out.println(users_location);
                    sendMessage(response);
                }
            }
            System.out.println(feedback);
        }
    }
    static int SERVER_PORT = 9999;
    public static void main(String[] args) throws Exception{
	// write your code here
        XMPPChatServer XMPPChatServerFSM = new XMPPChatServer(0);
        TcpTransportServer tcpFSM = new TcpTransportServer(5);
        tcpFSM.setServerPort(SERVER_PORT);
        tcpFSM.setReceiver(XMPPChatServerFSM);

        Dispatcher dis = new Dispatcher(false);
        dis.addFSM(XMPPChatServerFSM);
        dis.addFSM(tcpFSM);
        dis.start();

        System.out.println("Server is running on port 9999!");
        while(true){
            Thread.sleep(1);
        }
    }
}
