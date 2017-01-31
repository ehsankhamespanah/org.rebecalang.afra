package org.rebecalang.ide.afra.views.counterexample;

public class QueueContent extends Item {

	private String content;

	private MsgsrvQueue msgsrvQueue;

	public QueueContent(String name, String content, MsgsrvQueue msgsrvQueue) {
		super();
		this.name = name;
		this.content = content;
		this.msgsrvQueue = msgsrvQueue;
	}

	public MsgsrvQueue getMsgsrvQueue() {
		return msgsrvQueue;
	}

	public void setMsgsrvQueue(MsgsrvQueue msgsrvQueue) {
		this.msgsrvQueue = msgsrvQueue;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public QueueContent(String name, String content) {
		super();
		this.name = name;
		this.content = content;
	}

	@Override
	public Item getParent() {
		return getMsgsrvQueue();
	}

	public boolean equal(QueueContent q) {
		if(this.getName().equals(q.getName()) && this.getContent().equals(q.getContent()))
			return true;
		return false;
	}

}
