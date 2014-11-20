import org.fusesource.jansi.AnsiConsole;
import org.jeromq.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.*;
import static org.fusesource.jansi.Ansi.Color.*;

import static org.fusesource.jansi.Ansi.ansi;

public class Main {


    public static void main(String[] args) throws Exception {
        final ZMQ.Context context = ZMQ.context(1);
        if (args[0].equals("server")) {

            //  Socket to talk to clients
            final ZMQ.Socket responder = context.socket(ZMQ.REP);
            responder.bind("tcp://*:5555");

            ZMQ.Socket publisher = context.socket(ZMQ.PUB);
            publisher.bind("tcp://*:5563");

            VectorClockServer clockServer = new VectorClockServer(responder, publisher);
            int vectorSize = Integer.parseInt(args[1]);
            clockServer.initialize(vectorSize);

            System.out.println("Vector clock server initiated");

            while (!Thread.currentThread().isInterrupted()) {
                byte[] request = responder.recv(0);
                clockServer.processRequest(request);
            }

            responder.close();
            context.term();
        } else{
            //  Socket to talk to server
            System.out.println("Connecting to the Vector Clock Server");
            String serverIP = args[1];
            ZMQ.Socket requester = context.socket(ZMQ.REQ);
            requester.connect("tcp://" + serverIP + ":5555");

            requester.send(ByteConverter.toByte(ZeroMQMessageType.GET_INITIAL_VECTOR), 0);
            byte[] reply = requester.recv(0);
            final Vector<Integer> vector = (Vector) ByteConverter.fromByte(reply);

            requester.send(ByteConverter.toByte(ZeroMQMessageType.GET_MY_PROCESS_INDEX), 0);
            reply = requester.recv(0);
            Integer myIndex = (Integer) ByteConverter.fromByte(reply);

            final ZMQ.Socket subscriber = context.socket(ZMQ.SUB);
            subscriber.connect("tcp://"+serverIP+":5563");
            subscriber.subscribe(myIndex.toString().getBytes());
            System.out.println("My index is: " + myIndex);

            final VectorProcess vectorProcess = new VectorProcess(vector, myIndex);



            Thread clientSubscriberThread = new Thread() {
                public void run() {
                    Integer processIndex = 0;
                    while (!isInterrupted()) {
                        // Read envelope with address
                        String address = subscriber.recvStr();
                        // Read message contents
                        Vector newVector = (Vector) ByteConverter.fromByte(subscriber.recv());
                        vectorProcess.receiveMessage(newVector);
                        vectorProcess.printVector();
                    }
                }
            };
            clientSubscriberThread.start();


            while (!Thread.currentThread().isInterrupted()) {
                System.out.print(
                        "-------------------------------------- \n" +
                        "Write a command number and press enter: \n" +
                                " 1 - Execute internal event \n" +
                                " 2 - Send a message to a process \n" +
                        "-------------------------------------- \n"
                );
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                try {
                    String input = br.readLine();
                    if(input == null)
                        continue;
                    if (input.equals("1")) {
                        vectorProcess.executeInternalEvent();
                        vectorProcess.printVector();
                    }
                    if (input.equals("2")) {
                        Vector possibleDestinations = new Vector<Integer>();
                        for (int i = 0; i < vector.size(); i++) {
                            if (i != myIndex)
                                possibleDestinations.add(new Integer(i));
                        }
                        System.out.println("Select the process:");
                        input = "";
                        for (int i = 0; i < possibleDestinations.size(); i++)
                            System.out.println("Process number " + possibleDestinations.get(i));

                        input = br.readLine();
                        if (possibleDestinations.contains(Integer.parseInt(input))) {
                            vectorProcess.sendMessageTo(input, requester);
                        }
                    }

                } catch (IOException ioe) {
                    System.out.println("IO error!!!211!!");
                    System.exit(1);
                }
            }
            requester.close();
            context.term();

        }
    }


}
