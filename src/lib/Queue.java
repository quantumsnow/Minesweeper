package lib;

public class Queue implements java.io.Serializable {
	private static final long serialVersionUID = 3262762791072936157L;

	private class QueueLink implements java.io.Serializable {
		private static final long serialVersionUID = -4766738381295655644L;
		QueueLink nextLink;
		Object content;

		QueueLink(QueueLink nextLink, Object content) {
			this.nextLink = nextLink;
			this.content = content;
		}
	}

	private QueueLink head, tail;

	public Queue() {
		this.head = new QueueLink(null, null);
		this.tail = this.head;
	}

	public boolean isEmpty() {
		return this.head == this.tail;
	}

	public void enqueue(Object content) {
		if (content != null) {
			this.tail.content = content;
			this.tail.nextLink = new QueueLink(null, null);
			this.tail = this.tail.nextLink;
		}
	}

	public void dequeue() {
		if (!this.isEmpty()) {
			this.head = this.head.nextLink;
		}
	}

	public Object front() {
		return this.head.content;
	}

	@Override
	public String toString() {
		QueueLink current = this.head;
		String stringQueue = "";

		while (current != this.tail) {
			stringQueue += current.content.toString() + "\n";
			current = current.nextLink;
		}
		return stringQueue;
	}
}
