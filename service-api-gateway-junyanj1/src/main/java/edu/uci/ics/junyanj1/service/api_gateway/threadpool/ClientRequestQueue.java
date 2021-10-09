package edu.uci.ics.junyanj1.service.api_gateway.threadpool;

public class ClientRequestQueue {
    private ListNode head;
    private ListNode tail;

    public ClientRequestQueue() {
        this.head = null;
        this.tail = null;
    }

    public synchronized void enqueue(ClientRequest clientRequest) {
        ListNode previousTail = tail;
        tail = new ListNode(clientRequest,null);
        if (isEmpty())
            head = tail;
        else
            previousTail.setNext(tail);
    }

    public synchronized ClientRequest dequeue() {
        if (isEmpty())
            return null;
        ClientRequest request = head.getClientRequest();
        head = head.getNext();
        if (isEmpty())
            tail = null;
        return request;
    }

    boolean isEmpty() {
        return head == null;
    }
}
