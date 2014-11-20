import org.jeromq.ZMQ;

import java.util.Vector;

public class VectorClockServer {
    private final ZMQ.Socket responder;
    private final ZMQ.Socket publisher;
    private Vector<Integer> vector;
    Integer processIndex;

    public VectorClockServer(ZMQ.Socket responder, ZMQ.Socket publisher) {
        this.responder = responder;
        this.publisher = publisher;
    }

    public void initialize(int vectorSize) {
         vector = new Vector<Integer>(vectorSize);
        for (int i = 0; i < vectorSize; i++) {
            vector.add(new Integer(0));
        }
        processIndex = 0;
    }

    public void processRequest(byte[] request) {
        ZeroMQMessageType messageType = (ZeroMQMessageType) ByteConverter.fromByte(request);
        switch (messageType) {
            case GET_INITIAL_VECTOR:
                sendVector();
                break;
            case GET_MY_PROCESS_INDEX:
                sendProcessIndex();
                processIndex++;
                break;
            case SEND_A_MESSAGE:
                sendMessageToDestination();
        }
    }

    private void sendMessageToDestination() {
        responder.hasReceiveMore();
        String destination = (String) ByteConverter.fromByte(responder.recv(0));
        responder.hasReceiveMore();
        Vector senderVector = (Vector) ByteConverter.fromByte(responder.recv(0));
        responder.send("Ack", 0);

        publisher.sendMore(destination);
        publisher.send(ByteConverter.toByte(senderVector));
    }

    private void sendProcessIndex() {
        responder.send(ByteConverter.toByte(processIndex), 0);
    }

    private void sendVector() {
        responder.send(ByteConverter.toByte(vector), 0);
    }
}
