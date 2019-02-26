import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;

import java.util.concurrent.locks.ReentrantLock; 
/**
 * Class that implements the channel used by wizards and miners to communicate.
 */
public class CommunicationChannel {
	/**
	 * Creates a {@code CommunicationChannel} object. 
	 */
	private final ReentrantLock lock = new ReentrantLock(); 
	public static BlockingQueue<Message> wizzardQueue = new LinkedBlockingQueue<Message>();
	public static BlockingQueue<Message> minerQueue = new LinkedBlockingQueue<Message>();	

	public CommunicationChannel() {
	}

	/**
	 * Puts a message on the miner channel (i.e., where miners write to and wizards
	 * read from).
	 * 
	 * @param message
	 *            message to be put on the channel
	 */
	public void putMessageMinerChannel(Message message) {
		try {
			minerQueue.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		};
	}

	/**
	 * Gets a message from the miner channel (i.e., where miners write to and
	 * wizards read from).
	 * 
	 * @return message from the miner channel
	 */
	public Message getMessageMinerChannel() {
		Message message = null;
		try {
			message = minerQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return message;
	}

	/**
	 * Puts a message on the wizard channel (i.e., where wizards write to and miners
	 * read from).
	 * 
	 * @param message
	 *            message to be put on the channel
	 */
	
	
	public void putMessageWizardChannel(Message message) {
		lock.lock();
		if(message.getData() == Wizard.END) {
			int count = lock.getHoldCount();
			
			while(count > 0) {
				lock.unlock();
				count--;
			}
			return;
		}
		try {
			wizzardQueue.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		};
	}

	/**
	 * Gets a message from the wizard channel (i.e., where wizards write to and
	 * miners read from).
	 * 
	 * @return message from the miner channel
	 */
	public Message getMessageWizardChannel() {
		Message message = null;
		try {
			message = wizzardQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return message;
	}
}

