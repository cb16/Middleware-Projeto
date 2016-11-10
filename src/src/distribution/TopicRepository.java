package distribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TopicRepository {
	private ArrayList<String> topics;
	private Map<String, Message> topicPublicationsRepo;
	private Map<String, Publisher> topicPublisherRepo;
	
	public TopicRepository() {
		this.topics = new ArrayList<String>();
		this.topicPublicationsRepo = new HashMap<String, Message>();
		this.topicPublisherRepo = new HashMap<String, Publisher>();
	}

}
