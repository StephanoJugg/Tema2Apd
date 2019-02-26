import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * Class for a miner.
 */
public class Miner extends Thread {
	/**
	 * Creates a {@code Miner} object.
	 * 
	 * @param hashCount
	 *            number of times that a miner repeats the hash operation when
	 *            solving a puzzle.
	 * @param solved
	 *            set containing the IDs of the solved rooms
	 * @param channel
	 *            communication channel between the miners and the wizards
	 */
	 CommunicationChannel channel;
	 Set<Integer> solved;
	 Integer hashCount;
	 Semaphore sem = new Semaphore(1);
	 
	public Miner(Integer hashCount, Set<Integer> solved, CommunicationChannel channel) {
		this.channel = channel;
		this.hashCount = hashCount;
		this.solved = solved;
	}
	
	private static String encryptThisString(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            
            // convert to string
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
            String hex = Integer.toHexString(0xff & messageDigest[i]);
            if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
    
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
	
	private static String encryptMultipleTimes(String input, Integer count) {
        String hashed = input;
        for (int i = 0; i < count; ++i) {
            hashed = encryptThisString(hashed);
        }

        return hashed;
    }

	@Override
	public void run() {
		Message message = null;
		Message message2 = null;
		for(;;) {		
			try {
				sem.acquire();
				message = channel.getMessageWizardChannel();
				if(message.getData() == Wizard.EXIT) {
					break;
				}
					message2 = channel.getMessageWizardChannel();
					sem.release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			 
			synchronized(solved) {
				if (solved.contains(message2.getCurrentRoom())) {
					continue;
				} else {
					solved.add(message2.getCurrentRoom());
				}
			}
		
			Message newMessage = new Message(message.getCurrentRoom(), message2.getCurrentRoom(),
				encryptMultipleTimes(message2.getData(), hashCount));
		
			channel.putMessageMinerChannel(newMessage);
		}
	}
}

