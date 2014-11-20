import org.jeromq.ZMQ;
import java.util.Vector;

public class VectorProcess {
    private final Vector<Integer> vector;
    private final Integer myIndex;

    public VectorProcess(Vector<Integer> vector, Integer myIndex) {
        this.vector = vector;
        this.myIndex = myIndex;
    }

    public void executeInternalEvent() {
        Integer numberOfInternalEvents = vector.get(myIndex);
        numberOfInternalEvents++;
        vector.set(myIndex, numberOfInternalEvents);
    }

    public void printVector() {
        VectorPrinter printer = new VectorPrinter(vector, myIndex);
        printer.print();
    }

    public void sendMessageTo(String input, ZMQ.Socket requester) {
        requester.send(ByteConverter.toByte(ZeroMQMessageType.SEND_A_MESSAGE), ZMQ.SNDMORE);
        requester.send(ByteConverter.toByte(input), ZMQ.SNDMORE);
        requester.send(ByteConverter.toByte(vector), 0);
        byte[] reply = requester.recv(0);
    }

    public void receiveMessage(Vector newVector) {
        updateEachElementFromVectorsMaximumValue(newVector);
        executeInternalEvent();
    }

    private void updateEachElementFromVectorsMaximumValue(Vector newVector) {
        for(int i = 0; i < vector.size(); i++){
            if((Integer) newVector.get(i) > (Integer) vector.get(i))
                vector.set(i, (Integer) newVector.get(i));
        }
    }
}
