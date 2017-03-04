package org.rebecalang.afra.ideplugin.view.counterexample;

public class MsgsrvQueue extends Item {
	private RebecItem rebecItem;

	private QueueContent senderQueue;

	private QueueContent messageQueue;

	public QueueContent getMessageQueue() {
		return messageQueue;
	}

	public void setMessageQueue(QueueContent messageQueue) {
		this.messageQueue = messageQueue;
	}

	public RebecItem getRebec() {
		return rebecItem;
	}

	public void setRebec(RebecItem rebecItem) {
		this.rebecItem = rebecItem;
	}

	public QueueContent getSenderQueue() {
		return senderQueue;
	}

	public void setSenderQueue(QueueContent senderQueue) {
		this.senderQueue = senderQueue;
	}

	public MsgsrvQueue(RebecItem rebecItem) {
		super();
		this.rebecItem = rebecItem;
	}

	@Override
	public Item getParent() {
		return getRebec();
	}

}
